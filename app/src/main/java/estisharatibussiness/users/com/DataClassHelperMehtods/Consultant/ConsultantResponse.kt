package estisharatibussiness.users.com.DataClassHelperMehtods.Consultant

data class ConsultantResponse(
    val `data`: ArrayList<Data>,
    val sorting_options: ArrayList<String>,
    val status: String,
    val message: String
)