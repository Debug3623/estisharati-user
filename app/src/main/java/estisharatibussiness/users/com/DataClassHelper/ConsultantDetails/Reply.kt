package estisharatibussiness.users.com.DataClassHelper.ConsultantDetails

data class Reply(
    val comment: String,
    val consultant_id: Int,
    val course_id: Int,
    val created_at: String,
    val id: Int,
    val parent_id: Int,
    val review: String,
    val user: User,
    val user_id: Int
)