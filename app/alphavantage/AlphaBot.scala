package alphavantage

import javax.inject.Inject

import play.api.libs.json.Json
import play.api.libs.ws.WSClient

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.ExecutionContext.Implicits.global

class AlphaBot(ws: WSClient) {
  import AlphaBot._

  def lastPriceForTicker(ticker: String): BigDecimal = {
    //FIXME might need fix - move Future to controller
    val x = ws.url(urlForTickerDaily(ticker)).get().map { response =>
      (response.json \ "Time Series (Daily)" \ "2018-07-26" \ "4. close").as[BigDecimal]
    }
    Await.ready(x, 2 seconds).value.get.getOrElse(BigDecimal(0))
  }

}

object AlphaBot {

  val API_KEY = "7X68QKIMXY5W27VN"

  val BASE_URL = s"https://www.alphavantage.co/query?apikey=$API_KEY"

  def urlForTickerDaily(ticker: String) =
    BASE_URL + s"&symbol=$ticker" + s"&function=TIME_SERIES_DAILY"



}

