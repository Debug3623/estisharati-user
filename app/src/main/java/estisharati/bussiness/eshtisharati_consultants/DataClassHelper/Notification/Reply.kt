package estisharati.bussiness.eshtisharati_consultants.DataClassHelper.Notification

data class Reply(
    val category_id: String,
    val comment: String,
    val consultant_id: String,
    val course_id: String,
    val created_at: String,
    val id: String,
    val parent_id: String,
    val review: String,
    val user: User,
    val user_id: String
)