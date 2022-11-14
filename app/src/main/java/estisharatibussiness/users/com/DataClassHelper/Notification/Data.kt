package estisharatibussiness.users.com.DataClassHelper.Notification

data class Data(
    val body: String,
    val created_at: String,
    val details: ArrayList<Details>?,
    val id: String,
    val title: String,
    val type: String
)