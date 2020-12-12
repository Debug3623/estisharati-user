package digital.upbeat.estisharati_user.DataClassHelper.Notification

data class Data(
    val body: String,
    val created_at: String,
    val details: Details,
    val id: String,
    val title: String,
    val type: String
)