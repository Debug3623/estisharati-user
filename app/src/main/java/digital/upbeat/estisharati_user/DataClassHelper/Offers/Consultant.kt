package digital.upbeat.estisharati_user.DataClassHelper.Offers

data class Consultant(
    val consultant: ConsultantX,
    val consultant_id: Int,
    val course_id: Any,
    val discount_rate: String,
    val enddate: String,
    val id: Int,
    val offerprice: Double,
    val startdate: String,
    val type: String
)