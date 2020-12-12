package digital.upbeat.estisharati_user.DataClassHelper.Consultant

data class ConsultantResponse(
    val `data`: ArrayList<Data>,
    val sorting_options: ArrayList<String>,
    val status: String
)