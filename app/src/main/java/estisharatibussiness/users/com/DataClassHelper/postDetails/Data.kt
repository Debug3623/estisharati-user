package estisharatibussiness.users.com.DataClassHelper.postDetails

data class Data(
    val comments: ArrayList<Comment>,
    val content: String,
    val id: Int,
    val user: UserX
)