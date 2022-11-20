package estisharatibussiness.users.com.DataClassHelperMehtods.PaymentMethodList

data class PMResponse(
    val status: String,
    val message: String,
    val `data`: ArrayList<Data>

)