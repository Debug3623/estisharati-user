package estisharatibussiness.users.com.DataClassHelper.SurveyList

data class SurveyListResponse(
    val `data`: ArrayList<Data>,
    val status: String,
    val message: String
)