package controllers

import java.io.StringReader
import java.nio.file.Paths
import javax.inject._

import datasources.AlphaVantageClient
import core.{Stock, StockDB, StockInfo, TxPoint}
import models.GoogleParser
import org.apache.commons.csv.CSVFormat
import play.api._
import play.api.db.Database
import play.api.mvc._
import modelviews.StockPortOverviewMV
import play.api.libs.ws.WSClient

import scala.concurrent.ExecutionContext

@Singleton
class HomeController @Inject()(cc: ControllerComponents,
                               database: Database,
                               ws: WSClient) extends AbstractController(cc) {


  def index() = Action { implicit request: Request[AnyContent] =>
    val stocks: Map[StockInfo, List[TxPoint]] = database.withConnection { implicit c =>
      StockDB.getAll()
    }

    //val ab = new AlphaBot(ws)
    val positions = stocks.map { case (info, points) =>
      //val lastPrice = ab.lastPriceForTicker(info.ticker)
      StockPortOverviewMV(name=info.name,
        ticker=info.ticker,
        purchaseValue=Stock.purchaseValue(points),
        closePrice=info.closePrice,
        dayChange=info.dayChange,
        marketValue=Stock.marketValue(points, info.closePrice),
        totalUnitsOwned=Stock.totalUnitsOwned(points))
    }.toList

    Ok(views.html.index(positions))
  }

}
