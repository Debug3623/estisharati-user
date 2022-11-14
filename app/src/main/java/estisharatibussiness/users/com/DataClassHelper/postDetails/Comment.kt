package estisharatibussiness.users.com.DataClassHelper.postDetails

data class Comment(
    val commented_at: String,
    val comments: String,
    val id: Int,
    val user: User
)