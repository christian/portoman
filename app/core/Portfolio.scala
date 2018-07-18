package core

import java.sql.Connection

import anorm.SqlParser.{double, int, str}
import anorm.{RowParser, SQL, SqlParser, ~}
import modelviews.TrxListMV

case class TxPoint(typ: Int, units: Int, price: BigDecimal, commission: BigDecimal)

object TxPoint {

  /**
    * How much was paid for it.
    * @param data
    * @return
    */
  def purchaseValue(data: List[TxPoint]): BigDecimal =
    data.foldLeft(BigDecimal(0)) {(acc, s) => acc + s.typ * s.units * s.price + s.commission}


  /**
    * What is the current market value.
    * @param stock
    * @return
    */
  def marketValue(stock: TxPoint): BigDecimal = stock.units * stock.price


  /**
    * What is the return value.
    * @param purchaseValue
    * @param marketValue
    * @return
    */
  def returnValue(purchaseValue: BigDecimal, marketValue: BigDecimal): BigDecimal = marketValue - purchaseValue
}

case class StockInfo(name: String, ticker: String)

case class Stock(info: StockInfo, points: List[TxPoint])

object StockDB {

  val parser: RowParser[Stock] = str("name") ~
    str("ticker") ~
    str("type") ~
    int("units") ~
    double("price") ~ //FIXME loosing prevcision ?
    double("commission") map {
    case name ~ ticker ~ typ ~ units ~ price ~ commission =>
      play.Logger.info(typ)
      val intTyp = if (typ == "Buy") 1 else -1
      Stock(StockInfo(name, ticker), List(TxPoint(intTyp, units, BigDecimal(price), BigDecimal(commission))))
  }

  def getAll()(implicit db: Connection): List[Stock] = {
    val b = Map[StockInfo, List[TxPoint]]()
    val stocks = SQL("select t.type, t.units, t.price, t.commission, t.created_at, t.notes, s.name, s.ticker " +
      "from transactions t join securities s on t.security_id = s.id order by t.id asc;")
      .as(parser.*)

    val t = stocks.groupBy(stock => stock.info).map { case (stockInfo, list) => stockInfo -> list.flatMap(_.points) }
    play.Logger.info(t.toString)
    stocks
  }

}