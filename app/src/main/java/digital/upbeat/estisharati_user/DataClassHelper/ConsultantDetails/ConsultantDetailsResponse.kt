package digital.upbeat.estisharati_user.DataClassHelper.ConsultantDetails

data class ConsultantDetailsResponse(
    val city: String,
    var comments: ArrayList<Comment>,
    val chat_fee: String,
    val voice_fee: String,
    val video_fee: String,
    var offer_chat_fee: String,
    var offer_voice_fee: String,
    var offer_video_fee: String,
    var chat: Boolean,
    var voice: Boolean,
    var video: Boolean,
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
    val preview_video: String,
    val qualification_brief: String,
    val rate: String,
    var favourite: Boolean,
    val categories: ArrayList<Categories>
    )