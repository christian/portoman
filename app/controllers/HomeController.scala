package controllers

import java.nio.file.Paths
import javax.inject._

import play.api._
import play.api.mvc._

@Singleton
class HomeController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {


  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }


  def importFromCsv() =  Action(parse.multipartFormData) { implicit request =>
    request.body.file("csv_file").map { csvFile =>
      val filename = Paths.get(csvFile.filename).getFileName
      // csvFile.ref.moveTo(Paths.get(s"/tmp/$filename"), replace = true) // possible race condition

      // csvFile.ref.getAbsolutePath

      val source = scala.io.Source.fromFile(csvFile.ref.getAbsolutePath)
      val lines = try source.mkString finally source.close()

      play.Logger.info("Lines: "+ lines)

      Redirect(routes.HomeController.index()).flashing(
        "success" -> "File has been uploaded")
    }.getOrElse {
      Redirect(routes.HomeController.index()).flashing(
        "error" -> "Missing file")
    }
  }
}
