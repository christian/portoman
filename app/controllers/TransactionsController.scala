package controllers

import java.nio.file.Paths
import javax.inject.{Inject, Singleton}

import models.{GoogleParser, Transaction}
import modelviews.TrxListMV
import play.api.db.Database
import play.api.mvc.{AbstractController, AnyContent, ControllerComponents, Request}

@Singleton
class TransactionsController @Inject()(cc: ControllerComponents,
                                       database: Database) extends AbstractController(cc) {

  def list() = Action { implicit request: Request[AnyContent] =>
    val transactions = database.withConnection { implicit conn =>
      TrxListMV.getAll()
    }
    Ok(views.html.transactions(transactions))
  }

  def importTx() = Action { implicit request =>
    Ok(views.html.importTx())
  }

  def importFromCsv() =  Action(parse.multipartFormData) { implicit request =>
    request.body.file("csv_file").map { csvFile =>
      val filename = Paths.get(csvFile.filename).getFileName
      // csvFile.ref.moveTo(Paths.get(s"/tmp/$filename"), replace = true) // TODO possible race condition

      val source = scala.io.Source.fromFile(csvFile.ref.getAbsolutePath)
      val data = try source.mkString finally source.close()

      val lines = data.split("\n")
      play.Logger.info("Lines: "+ lines.toString)
      //lines.takeRight(lines.length - 1).foreach(line => GoogleParser.parse(line))

      GoogleParser.parse(data)(database)

      Redirect(routes.HomeController.index()).flashing(
        "success" -> "File has been uploaded")
    }.getOrElse {
      Redirect(routes.HomeController.index()).flashing(
        "error" -> "Missing file")
    }
  }

}
