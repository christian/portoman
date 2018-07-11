package models

import java.sql.Connection

import anorm.SqlParser.{int, scalar, str}
import anorm.{RowParser, SQL, ~}

case class Security(id: Option[Long],
                    name: String,
                    ticker: String,
                    closePrice: Option[BigDecimal])

object Security {

  val parser: RowParser[Security] = int("id") ~
    str("name") ~
    str("ticker") map {
    case id ~ name ~ ticker => Security(Some(id), name, ticker, None)
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