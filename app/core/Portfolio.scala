package core

import java.sql.Connection

import anorm.SqlParser.{double, int, str}
import anorm.{RowParser, SQL, SqlParser, ~}
import modelviews.TrxListMV

case class TxPoint(typ: Int, units: Int, price: BigDecimal, commission: BigDecimal)

object Stock {

  /**
    * How much was paid for it.
    * @param data
    * @return
    */
  def purchaseValue(data: List[TxPoint]): BigDecimal =
    data.foldLeft(BigDecimal(0)) {(acc, s) => acc + s.typ * s.units * s.price + s.commission}


  /**
    * What is the current market value.
    * @param data
    * @return
    */
  def marketValue(data: List[TxPoint], price: BigDecimal): BigDecimal =
    data.foldLeft(BigDecimal(0)) {(acc, s) => acc + s.typ * s.units} * price


  /**
    * What is the return value.
    * @param purchaseValue
    * @param marketValue
    * @return
    */
  def returnValue(purchaseValue: BigDecimal, marketValue: BigDecimal): BigDecimal = marketValue - purchaseValue


  /**
    * How many stocks for a symbol does user own.
    * @param data
    * @return
    */
  def totalUnitsOwned(data: List[TxPoint]): Int =
    data.foldLeft(0) {(acc, s) => acc + s.typ * s.units}

}

case class StockInfo(name: String,
                     ticker: String,
                     closePrice: BigDecimal,
                     dayChange: BigDecimal)

case class Stock(info: StockInfo, points: List[TxPoint]) {

  def isPositionClosed() =
    points.foldLeft(0) {(acc, s) => acc + s.typ} == 0

}

object StockDB {

  val parser: RowParser[Stock] = str("name") ~
    str("ticker") ~
    str("type") ~
    int("units") ~
    double("price") ~ //FIXME loosing prevcision ?
    double("commission") ~
    double("close_price") ~
    double("day_change") map {
    case name ~ ticker ~ typ ~ units ~ purchasePrice ~ commission ~ closePrice ~ dayChange =>
      val intTyp = if (typ == "Buy") 1 else -1
      Stock(
        StockInfo(name, ticker, BigDecimal(closePrice), dayChange),
        List(TxPoint(intTyp, units, BigDecimal(purchasePrice), BigDecimal(commission)))
      )
  }

  def getAll()(implicit db: Connection): Map[StockInfo, List[TxPoint]] = {
    val stocks = SQL("select t.type, t.units, t.price, t.commission, t.created_at, t.notes, s.name, s.ticker, " +
      "s.close_price, s.day_change " +
      "from transactions t join securities s on t.security_id = s.id order by t.id asc;")
      .as(parser.*)

    stocks.groupBy(stock => stock.info).map {
      case (stockInfo, list) => stockInfo -> list.flatMap(_.points)
    }
  }

}