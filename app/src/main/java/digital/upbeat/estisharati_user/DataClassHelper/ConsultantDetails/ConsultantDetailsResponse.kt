package digital.upbeat.estisharati_user.DataClassHelper.ConsultantDetails

data class ConsultantDetailsResponse(
    val city: String,
    var comments: ArrayList<Comment>,
    val consultant_cost: String,
    val offerprice: String,
    val offer_end: String,
    val is_subscribed: Boolean,
    val country: String,
    val course_count: Int,
    val email: String,
    val id: String,
    val image: String,
    val image_path: String,
    val job_title: String,
    val name: String,
    val qualification: String,
    val qualification_brief: String,
    val rate: String,
    var favourite: Boolean
)