package digital.upbeat.estisharati_user.DataClassHelper.SurveysQuestions

data class Question(
    val id: String,
    val image: String,
    val image_path: String,
    val options: List<Option>,
    val question: String,
    val survey_id: Int,
    var option_selected: String?
)