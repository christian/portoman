package modelviews

import java.sql.Connection
import java.util.Date

import anorm.{RowParser, SQL, SqlParser, ~}
import anorm.SqlParser.{date, double, int, str}
import models.{Security, Transaction}


case class TrxListMV(name: String,
                     ticker: String,
                     typ: String,
                     units: Int,
                     price: BigDecimal,
                     commission: BigDecimal,
                     createdAt: Date, //FIXME types?
                     notes: String)

object TrxListMV {
  val parser: RowParser[TrxListMV] = str("name") ~
    str("ticker") ~
    str("type") ~
    int("units") ~
    double("price") ~ //FIXME loosing prevcision ?
    double("commission") ~
    SqlParser.date("created_at") ~
    str("notes") map {
    case name ~ ticker ~ typ ~ units ~ price ~ commission ~ createdAt ~ notes =>
      TrxListMV(name, ticker, typ, units, BigDecimal(price), BigDecimal(commission), createdAt, notes)
  }

  def getAll()(implicit db: Connection): List[TrxListMV] = {
    SQL("select t.type, t.units, t.price, t.commission, t.created_at, t.notes, s.name, s.ticker " +
        "from transactions t join securities s on t.security_id = s.id;")
      .as(parser.*)
  }
}