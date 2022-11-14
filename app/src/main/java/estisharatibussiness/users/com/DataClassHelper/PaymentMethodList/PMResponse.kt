package estisharatibussiness.users.com.DataClassHelper.PaymentMethodList

data class PMResponse(
    val status: String,
    val message: String,
    val `data`: ArrayList<Data>

)