package digital.upbeat.estisharati_user.DataClassHelper.MyConsultation

data class MyConsultationResponse(
    val `data`: ArrayList<Data>,
    val message: String,
    val status: String,
)