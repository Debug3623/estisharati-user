package estisharatibussiness.users.com.DataClassHelper.StartCourse

data class Video(
    val chapter_title: String,
    val course_id: Int,
    val id: Int,
    val lessons: ArrayList<Lesson>,
    val resource_path: String
)