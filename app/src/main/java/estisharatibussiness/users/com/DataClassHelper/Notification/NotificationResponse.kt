package estisharatibussiness.users.com.DataClassHelper.Notification

data class NotificationResponse(
    val status: String,
    val message: String,
    val `data`: ArrayList<Data>
)