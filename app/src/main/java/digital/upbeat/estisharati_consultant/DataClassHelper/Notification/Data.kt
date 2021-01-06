package digital.upbeat.estisharati_consultant.DataClassHelper.Notification

data class Data(
    val body: String,
    val created_at: String,
    val details: ArrayList<Detail>,
    val id: Int,
    val title: String,
    val type: String,
)