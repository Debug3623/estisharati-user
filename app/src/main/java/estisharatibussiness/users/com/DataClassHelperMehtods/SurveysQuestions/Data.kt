package estisharatibussiness.users.com.DataClassHelperMehtods.SurveysQuestions

data class Data(
    val description: String,
    val id: String,
    val image: String,
    val image_path: String,
    val questions: ArrayList<Question>,
    val title: String
)