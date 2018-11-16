package models

import java.sql.Connection
import java.util.Date

import anorm.SqlParser.{int, scalar, str}
import anorm.{RowParser, SQL, SqlParser, ~}
import datasources.IEXInfo
import org.joda.time.LocalDateTime

case class Security(id: Option[Long],
                    name: String,
                    ticker: String,
                    closePrice: Option[BigDecimal],
                    updatedAt: Date)

object Security {

  val parser: RowParser[Security] = int("id") ~
    str("name") ~
    str("ticker") ~
    SqlParser.date("updatedAt") map {
    case id ~ name ~ ticker ~ updatedAt => Security(Some(id), name, ticker, None, updatedAt)
  }

  /**
    * Insert security in DB.
    * @param security
    * @param db
    * @return
    */
  def insert(security: Security)(implicit db: Connection): Option[Long] = {
    val id: Option[Long] =
      SQL("insert into securities(name, ticker, close_price) " +
        "values ({name}, {ticker}, {closePrice})")
        .on('name -> security.name,
          'ticker -> security.ticker,
          'closePrice -> security.closePrice).executeInsert()
    id
  }

  /**
    * Update latest price of a security.
    * @param ticker
    * @param db
    * @return true if the price could be updated
    *         false otherwise
    */
  def updateInfo(ticker: String, info: IEXInfo)(implicit db: Connection): Boolean = {
    val updatedLines = SQL("update securities set " +
      "close_price={closePrice}, " +
      "day_change={dayChange}, " +
      "day_change_percent={dayChangePercent}, " +
      "updated_at={updatedAt} " +
      "where ticker={ticker} limit 1")
      .on('ticker -> ticker,
                  'closePrice -> info.closePrice,
                  'dayChange -> info.change,
                  'dayChangePercent -> info.changePercent,
                  'updatedAt -> new Date())
      .executeUpdate()
    updatedLines == 1
  }

  /**
    * Return security by ticker if it exists.
    * @param ticker
    * @param db
    * @return
    */
  def get(ticker: String)(implicit db: Connection): Option[Security] = {
    SQL("select * from securities where ticker = {ticker}")
      .on('ticker -> ticker).as(parser.singleOpt)
  }

  /**
    * Get oldest updated Ticker by looking at the updated_at column
    * @param db
    * @return
    */
  def getOldestUpdated()(implicit db: Connection): Option[String] = {
    SQL("select ticker from securities order by updated_at asc limit 1")
      .as(scalar[String].singleOpt)
  }

  /**
    * Check that security exists
    * @param ticker
    * @param db
    * @return
    */
  def exists(ticker: String)(implicit db: Connection): Boolean = {
    val numRecords =
      SQL("select count(*) from securities where ticker = {ticker}")
        .on('ticker -> ticker).as(scalar[Long].single)
    numRecords != 0
  }

}