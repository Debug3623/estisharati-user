package digital.upbeat.estisharati_user.DataClassHelper.postDetails

data class Comment(
    val commented_at: String,
    val comments: String,
    val id: Int,
    val user: User
)