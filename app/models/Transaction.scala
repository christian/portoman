package models

import java.io.StringReader
import java.time.{LocalDate, LocalDateTime}
import java.time.format.DateTimeFormatter
import java.util.Date

import org.apache.commons.csv.CSVFormat

case class Transaction(id: Option[Long],
                       uuid: Option[String],
                       `type`: String,
                       securityId: Option[Long],
                       units: Long,
                       price: BigDecimal,
                       createdAt: LocalDateTime,
                       positionId: Option[Long])

case class Security(id: Option[Long],
                    name: String,
                    ticker: String,
                    closePrice: Option[BigDecimal])

case class Position(id: Option[Long],
                    status: String,
                    securityId: Option[Long],
                    totalUnits: Long,
                    totalCost: BigDecimal) // TODO replace with Money

//Symbol,Name,Type,Date,Shares,Price,Cash value,Commission,Notes
//ZAL,Zalando SE,Buy,"Feb 9, 2016",195,26.49,,47.74,Initial ZAL buy
object GoogleParser {

  def parse(data: String) = {

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

      // TODO need to add
      // - comments for transaction
      // - commision for transaction

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
        positionId=None
      )

      val cost = BigDecimal(record.get("Shares")) * BigDecimal(record.get("Price")) + BigDecimal(record.get("Commission"))
      val position = Position(
        id=None,
        status="Open",
        securityId=None,
        totalUnits=record.get("Shares").toLong,
        totalCost=cost
      )

      play.Logger.info(security.toString)
      play.Logger.info(position.toString)
      play.Logger.info(transaction.toString)
    }

  }

}


object Transaction {

//  def parse(line: String): Option[Transaction] = {
//
//  }
//
//  def insert(tx: Transaction) {
//    val id: Option[Long] =
//      SQL("insert into City(name, country) values ({name}, {country})")
//      .on('name -> "Cambridge", 'country -> "New Zealand").executeInsert()
//  }

//  def getAll(symbol: String): List[Transaction] = {
//    //List("")
//  }

}

object Security {

}

object Position {

}