package digital.upbeat.estisharati_user.DataClassHelper.PaymentMethodList

data class PMResponse(
    val status: String,
    val message: String,
    val `data`: ArrayList<Data>

)