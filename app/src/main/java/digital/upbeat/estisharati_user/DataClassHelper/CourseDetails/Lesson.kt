package digital.upbeat.estisharati_user.DataClassHelper.CourseDetails

data class Lesson(
    val chapter_id: Int,
    val course_id: Int,
    val ext: String,
    val filename: String,
    val id: Int,
    val lesson_file: String,
    val title: String,
    val type: String
)