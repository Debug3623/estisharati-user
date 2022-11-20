package estisharatibussiness.users.com.DataClassHelperMehtods.PaymentRequest

data class Details(
    val cardno: String,
    val cvv: String,
    val email: String,
    val expiry: String,
    val name: String
)