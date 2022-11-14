package estisharatibussiness.users.com.DataClassHelper.MyConsultation

data class MyConsultationResponse(
    val `data`: ArrayList<Data>,
    val message: String,
    val status: String,
)