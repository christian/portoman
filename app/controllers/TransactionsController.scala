package controllers

import javax.inject.{Inject, Singleton}

import models.Transaction
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

}
