package modelviews

import scala.math.BigDecimal.RoundingMode

/**
  * Class used only for display purposes. No logic should exist here.
  * @param name
  * @param ticker
  * @param closePrice
  * @param purchaseValue
  * @param marketValue
  */
case class StockPortOverviewMV(name: String,
                               ticker: String,
                               closePrice: BigDecimal,
                               dayChange: BigDecimal,
                               purchaseValue: BigDecimal,
                               marketValue: BigDecimal,
                               totalUnitsOwned: Int) {

  import StockPortOverviewMV._

  /**
    * Format as money.
    * @return
    */
  def purchaseValue_AsMoney(): String = formatter.format(purchaseValue)

  /**
    * Format as money.
    * @return
    */
  def marketValue_AsMoney(): String = formatter.format(marketValue)

  def closePrice_AsMoney(): String = formatter.format(closePrice)

  def gain: BigDecimal = marketValue - purchaseValue

  def gainPercent: BigDecimal = ((marketValue - purchaseValue) * 100 / purchaseValue).setScale(0, RoundingMode.UP)

  def daysGain = 0

  def gain_AsMoney(): String = formatter.format(gain)
}


object StockPortOverviewMV {
  lazy val formatter = java.text.NumberFormat.getCurrencyInstance
}