package datasources

import java.text.SimpleDateFormat
import java.util.Date

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import javax.inject.{Inject, Named}
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import play.api.libs.json.Json
import play.api.libs.ws.WSClient

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.ExecutionContext.Implicits.global

class AlphaVantageClient(ws: WSClient) {
  import AlphaVantageClient._

  def lastPriceForTicker(ticker: String): BigDecimal = {
    //FIXME might need fix - move Future to controller

    play.Logger.info(previousTradingDay())
    val x = ws.url(urlForTickerDaily(ticker)).get().map { response =>
      (response.json \ "Time Series (Daily)" \ previousTradingDay() \ "4. close").as[BigDecimal]
    }
    Await.ready(x, 2 seconds).value.get.getOrElse(BigDecimal(0))
  }

}

case object Tick

class AlphaVantageScheduler @Inject()(actorSystem: ActorSystem,
                                      @Named("alpha-vantage-fetcher") someActor: ActorRef) {

  actorSystem.scheduler.schedule(
    initialDelay = 0.microseconds,
    interval = 10.seconds,
    receiver = someActor,
    message = Tick
  )

}

object AlphaVantageActor {
  def props = Props[AlphaVantageActor]

  //case class SayHello(name: String)
}

class AlphaVantageActor extends Actor {
 // import HelloActor._

  def receive = {
    case Tick =>
      play.Logger.info("Send tick");
  }
}


object AlphaVantageClient {

  val API_KEY = "7X68QKIMXY5W27VN"

  val BASE_URL = s"https://www.alphavantage.co/query?apikey=$API_KEY"

  def urlForTickerDaily(ticker: String) =
    BASE_URL + s"&symbol=$ticker" + s"&function=TIME_SERIES_DAILY"


  // TODO change to previous trading day to be more general
  // TODO move to utilities
  def previousTradingDay(): String = {
    val dateFormat = DateTimeFormat.forPattern("yyyy-MM-dd")
    val today: DateTime = new DateTime()
    val yesterday = today.minusDays(1)
    dateFormat.print(yesterday)
  }

}



trait PriceBot {

  def closePriceForSymbols(sym: List[String]) = ???

}