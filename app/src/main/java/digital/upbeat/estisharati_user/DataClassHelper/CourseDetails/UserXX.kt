package digital.upbeat.estisharati_user.DataClassHelper.CourseDetails

data class UserXX(
    val id: Int,
    val image: String,
    val image_path: String,
    val name: String,
    val qualification: List<Qualification>,
    val course_count: String,
    val rate: String
)