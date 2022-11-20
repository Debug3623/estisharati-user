package estisharatibussiness.users.com.DataClassHelperMehtods.postDetails

data class Data(
    val comments: ArrayList<Comment>,
    val content: String,
    val id: Int,
    val user: UserX
)