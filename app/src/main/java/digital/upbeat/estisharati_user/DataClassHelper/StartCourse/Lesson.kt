package digital.upbeat.estisharati_user.DataClassHelper.StartCourse

data class Lesson(
    val chapter_id: String,
    val course_id: String,
    val ext: String,
    val filename: String,
    val id: String,
    var watched: Boolean,
    val lesson_file: String,
    val title: String,
    val type: String,
    var position: Int,
)