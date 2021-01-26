package digital.upbeat.estisharati_user.DataClassHelper.Notification

data class NotificationResponse(
    val status: String,
    val message: String,
    val `data`: ArrayList<Data>
)