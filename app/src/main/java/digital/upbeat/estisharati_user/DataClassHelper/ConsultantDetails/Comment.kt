package digital.upbeat.estisharati_user.DataClassHelper.ConsultantDetails

data class Comment(
    val comment: String,
    val consultant_id: String,
    val course_id: Int,
    val created_at: String,
    val id: Int,
    val parent_id: Any,
    val replies: ArrayList<Reply>,
    val review: String,
    val user: UserX,
    val user_id: Int
)