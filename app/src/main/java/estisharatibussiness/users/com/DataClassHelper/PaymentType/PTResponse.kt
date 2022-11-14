package estisharatibussiness.users.com.DataClassHelper.PaymentType

data class PTResponse(
    val `data`: ArrayList<Data>,
    val status: String,
    val message: String
)