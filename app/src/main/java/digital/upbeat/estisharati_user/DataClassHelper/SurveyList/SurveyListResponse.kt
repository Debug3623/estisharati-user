package digital.upbeat.estisharati_user.DataClassHelper.SurveyList

data class SurveyListResponse(
    val `data`: ArrayList<Data>,
    val status: String,
    val message: String
)