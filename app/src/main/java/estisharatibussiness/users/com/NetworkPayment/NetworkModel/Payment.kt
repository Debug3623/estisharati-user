package estisharatibussiness.users.com.networkPayment.NetworkModel

data class Payment(
    val _id: String,
    val _links: Links,
    val amount: Amount,
    val merchantOrderReference: String,
    val orderReference: String,
    val outletId: String,
    val state: String,
    val updateDateTime: String
)