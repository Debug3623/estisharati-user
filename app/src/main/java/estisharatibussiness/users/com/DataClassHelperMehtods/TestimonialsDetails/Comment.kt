package estisharatibussiness.users.com.DataClassHelperMehtods.TestimonialsDetails

data class Comment(
    val comment: String,
    val created_at: String,
    val experience_id: String,
    val id: String,
    val user: User,
    val user_id: String
)