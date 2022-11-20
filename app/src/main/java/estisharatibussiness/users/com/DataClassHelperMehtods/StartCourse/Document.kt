package estisharatibussiness.users.com.DataClassHelperMehtods.StartCourse

data class Document(
    val chapter_title: String,
    val course_id: Int,
    val id: Int,
    val lessons: ArrayList<Lesson>,
    val resource_path: String
)