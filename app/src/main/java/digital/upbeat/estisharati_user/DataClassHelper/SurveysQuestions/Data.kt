package digital.upbeat.estisharati_user.DataClassHelper.SurveysQuestions

data class Data(
    val description: String,
    val id: String,
    val image: String,
    val image_path: String,
    val questions: ArrayList<Question>,
    val title: String
)