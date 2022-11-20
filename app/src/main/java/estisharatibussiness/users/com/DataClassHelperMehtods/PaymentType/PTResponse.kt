package estisharatibussiness.users.com.DataClassHelperMehtods.PaymentType

data class PTResponse(
    val `data`: ArrayList<Data>,
    val status: String,
    val message: String
)