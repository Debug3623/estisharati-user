package digital.upbeat.estisharati_consultant.DataClassHelper.Notification

data class Reply(
    val comment: String,
    val course_id: Int,
    val created_at: String,
    val id: Int,
    val parent_id: Int,
    val review: String,
    val user: User,
    val user_id: Int
)