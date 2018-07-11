package models

import java.sql.Connection

import anorm.SQL

case class Position(id: Option[Long],
                    status: String,
                    securityId: Option[Long],
                    totalUnits: Long,
                    totalCost: BigDecimal) // TODO replace with Money

object Position {

  /**
    * Insert position in DB.
    * @param position
    * @param securityId
    * @param db
    * @return
    */
  def insert(position: Position, securityId: Long)(implicit db: Connection): Option[Long] = {
    val id: Option[Long] =
      SQL("insert into positions(status, security_id, total_units, total_cost) " +
        "values ({status}, {security_id}, {total_units}, {total_cost})")
        .on('status -> position.status,
          'security_id -> securityId,
          'total_units -> position.totalUnits,
          'total_cost -> position.totalCost).executeInsert()
    id
  }

  /**
    * Get all positions.
    */
  def getAll() = {
  }

  def get() = {}

}


