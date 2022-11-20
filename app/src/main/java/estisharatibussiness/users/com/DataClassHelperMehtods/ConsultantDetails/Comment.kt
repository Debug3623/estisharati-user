package estisharatibussiness.users.com.DataClassHelperMehtods.ConsultantDetails

data class Comment(
    val comment: String,
    val consultant_id: String,
    val course_id: Int,
    val created_at: String,
    val id: String,
    val parent_id: Any,
    val replies: ArrayList<Reply>,
    val review: String,
    val user: User,
    val user_id: Int
)