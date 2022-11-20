package estisharatibussiness.users.com.DataClassHelperMehtods.Notification

data class NotificationResponse(
    val status: String,
    val message: String,
    val `data`: ArrayList<Data>
)