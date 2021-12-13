package digital.upbeat.estisharati_user.DataClassHelper.postDetails

data class Data(
    val comments: ArrayList<Comment>,
    val content: String,
    val id: Int,
    val user: UserX
)