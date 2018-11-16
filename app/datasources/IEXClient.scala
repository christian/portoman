package datasources

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import javax.inject.{Inject, Named}
import models.Security
import play.api.db.Database
import play.api.libs.ws.WSClient

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

case class IEXInfo(closePrice: BigDecimal, change: BigDecimal, changePercent: BigDecimal)

class IEXClient(ws: WSClient) {
  import IEXClient._

  def infoForTicker(ticker: String): IEXInfo = {
    //FIXME might need fix - move Future to controller

    val x = ws.url(urlForTickerDaily(ticker)).get().map { response =>
      ((response.json \ "latestPrice").as[BigDecimal],
        (response.json \ "change").as[BigDecimal],
        (response.json \ "changePercent").as[BigDecimal])
    }
    val info = Await.ready(x, 2 seconds).value.get.getOrElse((BigDecimal(0), BigDecimal(0), BigDecimal(0)))
    IEXInfo.tupled(info)
  }

}

object IEXClient {
  val BASE_URL = s"https://api.iextrading.com/1.0/stock/"

  def urlForTickerDaily(ticker: String) =
    BASE_URL + s"$ticker" + "/quote"

}

class IEXScheduler @Inject()(actorSystem: ActorSystem,
                             @Named("iex-fetcher") someActor: ActorRef) {
  import IEXActor._

  actorSystem.scheduler.schedule(
    initialDelay = 0.microseconds,
    interval = 10.seconds,
    receiver = someActor,
    message = Tick
  )

}

object IEXActor {
  def props = Props[AlphaVantageActor]

  case object Tick
}

class IEXActor @Inject()(ws: WSClient, db: Database) extends Actor {
  import IEXActor._

  def receive = {
    case Tick =>
      play.Logger.info("IEX tick")
      db.withConnection{ implicit conn =>
        Security.getOldestUpdated()
      }.foreach { ticker => {
        val iexClient = new IEXClient(ws)
        val info: IEXInfo = iexClient.infoForTicker(ticker) // TODO this could fail
        play.Logger.debug(s"Latest price for $ticker: ${info.closePrice} (${IEXClient.urlForTickerDaily(ticker)})")
        db.withConnection { implicit conn =>
          if (Security.updateInfo(ticker, info)) {
            play.Logger.debug(s"Update price for $ticker to ${info.closePrice}")
          } else {
            play.Logger.error(s"Could not update price for $ticker to ${info.closePrice}")
          }
        }
      }}
  }
}