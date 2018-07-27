package controllers

import java.io.StringReader
import java.nio.file.Paths
import javax.inject._

import core.{Stock, StockDB, StockInfo, TxPoint}
import models.GoogleParser
import org.apache.commons.csv.CSVFormat
import play.api._
import play.api.db.Database
import play.api.mvc._
import modelviews.StockPortOverviewMV

@Singleton
class HomeController @Inject()(cc: ControllerComponents,
                               database: Database) extends AbstractController(cc) {


  def index() = Action { implicit request: Request[AnyContent] =>
    val stocks: Map[StockInfo, List[TxPoint]] = database.withConnection { implicit c =>
      StockDB.getAll()
    }

    val positions = stocks.map { case (info, points) =>
      StockPortOverviewMV(info.name, info.ticker, Stock.purchaseValue(points))
    }.toList

    Ok(views.html.index(positions))
  }

}
