package digital.upbeat.estisharati_user.DataClassHelper.Offers

data class Course(
    val consultant_id: Any,
    val course: CourseX,
    val course_id: Int,
    val discount_rate: String,
    val enddate: String,
    val id: Int,
    val offerprice: Int,
    val startdate: String,
    val type: String
)