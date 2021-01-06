package digital.upbeat.estisharati_consultant.DataClassHelper.Notification

data class Detail(
    val comment: String,
    val course_id: Int,
    val created_at: String,
    val id: Int,
    val parent_id: Any,
    val replies: List<Reply>,
    val review: String,
    val type: String,
    val user: User,
    val user_id: Int
)