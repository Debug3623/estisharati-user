package estisharatibussiness.users.com.DataClassHelperMehtods.SurveyList

data class SurveyListResponse(
    val `data`: ArrayList<Data>,
    val status: String,
    val message: String
)