package estisharatibussiness.users.com.DataClassHelperMehtods.Offers

data class Consultant(
    val consultant: ConsultantX,
    val consultant_id: String,
    val course_id: Any,
    val discount_rate: String,
    val enddate: String,
    val id: Int,
    val offerprice: String,
    val startdate: String,
    val type: String
)