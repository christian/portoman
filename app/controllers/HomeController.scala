package controllers

import java.io.StringReader
import java.nio.file.Paths
import javax.inject._

import models.GoogleParser
import org.apache.commons.csv.CSVFormat
import play.api._
import play.api.db.Database
import play.api.mvc._

@Singleton
class HomeController @Inject()(cc: ControllerComponents,
                               database: Database) extends AbstractController(cc) {


  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }

}
