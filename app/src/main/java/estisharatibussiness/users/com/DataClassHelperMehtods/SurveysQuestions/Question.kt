package estisharatibussiness.users.com.DataClassHelperMehtods.SurveysQuestions

data class Question(
    val id: String,
    val image: String,
    val image_path: String,
    val options: ArrayList<Option>,
    val question: String,
    val survey_id: Int,
    var option_selected: String?
)