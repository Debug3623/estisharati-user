package estisharatibussiness.users.com.DataClassHelper.TestimonialsDetails

data class Data(
    val comments: ArrayList<Comment>,
    val comments_count: String,
    val consultant_category: String,
    val category_id: String,
    val consultant_id: String,
    val course_id: String,
    val experience: String,
    val id: String,
    val service_name: String,
    val type: String,
    val updated_at: String,
    val user: User,
    val user_id: String
)