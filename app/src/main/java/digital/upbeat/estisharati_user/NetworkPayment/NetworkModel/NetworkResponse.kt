package digital.upbeat.estisharati_user.networkPayment.NetworkModel

data class NetworkResponse(
    val _embedded: Embedded,
    val _id: String,
    val _links: LinksX,
    val action: String,
    val amount: AmountX,
    val billingAddress: BillingAddress,
    val createDateTime: String,
    val emailAddress: String,
    val formattedAmount: String,
    val formattedOrderSummary: FormattedOrderSummary,
    val language: String,
    val merchantAttributes: MerchantAttributes,
    val merchantDefinedData: MerchantDefinedData,
    val merchantOrderReference: String,
    val outletId: String,
    val paymentMethods: PaymentMethods,
    val reference: String,
    val referrer: String,
    val shippingAddress: ShippingAddress,
    val type: String
)