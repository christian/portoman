package modelviews

case class StockPortOverviewMV(name: String,
                               ticker: String,
                               purchaseValue: BigDecimal) {

  def moneyPurchaseValue(): String = {
    val formatter = java.text.NumberFormat.getCurrencyInstance
    formatter.format(purchaseValue)
  }

}
