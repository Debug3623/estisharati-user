package digital.upbeat.estisharati_consultant.DataClassHelper.Notification

data class NotificationsResponse(
    val status: String,
    val message: String,
    val `data`: ArrayList<Data>
)