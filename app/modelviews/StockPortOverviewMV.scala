package modelviews

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
                               purchaseValue: BigDecimal,
                               marketValue: BigDecimal) {

  def moneyPurchaseValue(): String = {
    val formatter = java.text.NumberFormat.getCurrencyInstance
    formatter.format(purchaseValue)
  }

}
