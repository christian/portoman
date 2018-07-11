package models

import java.io.StringReader
import java.sql.Connection
import java.time.{LocalDate, LocalDateTime}
import java.time.format.DateTimeFormatter
import java.util.{Date, UUID}

import play.api.db.Database
import anorm._
import org.apache.commons.csv.CSVFormat
import anorm.SqlParser._
import models._

case class Transaction(id: Option[Long],
                       uuid: Option[String],
                       `type`: String,
                       securityId: Option[Long],
                       units: Long,
                       price: BigDecimal,
                       createdAt: LocalDateTime,
                       positionId: Option[Long],
                       commission: BigDecimal,
                       notes: String)

//Symbol,Name,Type,Date,Shares,Price,Cash value,Commission,Notes
//ZAL,Zalando SE,Buy,"Feb 9, 2016",195,26.49,,47.74,Initial ZAL buy
object GoogleParser {

  def parse(data: String)(implicit db: Database) = {

    val records = CSVFormat.RFC4180
      .withHeader("Symbol", "Name", "Type", "Date", "Shares", "Price", "Cash value", "Commission", "Notes")
      .withFirstRecordAsHeader()
      .parse(new StringReader(data))

    records.forEach { record =>
      play.Logger.info(record.toString)

      val security = Security(
        id=None,
        name=record.get("Name"),
        ticker=record.get(0), // Funny, "Symbol" doesn't work
        closePrice=None
      )

      val formatter = DateTimeFormatter.ofPattern("MMM d, uuuu")
      val dateTime = LocalDateTime.from(LocalDate.parse(record.get("Date"), formatter).atStartOfDay())
      val transaction = Transaction(
        id=None,
        uuid=None,
        `type`=record.get("Type"), // TODO create an enum
        securityId=None,
        units=record.get("Shares").toLong,
        price=BigDecimal(record.get("Price")),
        createdAt=dateTime,
        positionId=None,
        commission=BigDecimal(record.get("Commission")),
        notes=record.get("Notes")
      )

      val cost = BigDecimal(record.get("Shares")) * BigDecimal(record.get("Price")) + BigDecimal(record.get("Commission"))
      val position = Position(
        id=None,
        status="Open",
        securityId=None,
        totalUnits=record.get("Shares").toLong,
        totalCost=cost
      )

      play.Logger.debug("Parser: " + security.toString)
      play.Logger.debug("Position: " + position.toString)
      play.Logger.debug("Transaction: " + transaction.toString)

      db.withTransaction {implicit c =>

        val securityId = Security.get(security.ticker) match {
          case Some(s) => s.id.get
          case None => Security.insert(security).get
        }

        val positionId = Position.insert(position, securityId).get
        val transactionId = Transaction.insert(transaction, securityId, positionId).get
      }

    }

  }

}

// TODO unit tests for DB functions. To check types and functions: what is in DB, is out.

object Transaction {

  /**
    * Insert transaction in DB.
    * @param transaction
    * @param securityId
    * @param positionId
    * @param db
    * @return
    */
  def insert(transaction: Transaction, securityId: Long, positionId: Long)(implicit db: Connection): Option[Long] = {
    val uuid = UUID.randomUUID().toString
    val id: Option[Long] =
      SQL("insert into transactions(uuid, type, security_id, units, price, created_at, position_id, commission, notes) " +
          "values ({uuid}, {type}, {security_id}, {units}, {price}, {created_at}, {position_id}, {commission}, {notes})")
      .on('uuid -> uuid,
          'type -> transaction.`type`,
          'security_id -> securityId,
          'units -> transaction.units,
          'price -> transaction.price,
          'created_at -> transaction.createdAt,
          'position_id -> positionId,
          'commission -> transaction.commission,
          'notes -> transaction.notes).executeInsert()
    id
  }

}


