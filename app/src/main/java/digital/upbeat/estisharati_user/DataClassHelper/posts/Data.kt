package digital.upbeat.estisharati_user.DataClassHelper.posts

data class Data(
    val comments_count: Int,
    val content: String,
    val id: String,
    val user: User
)