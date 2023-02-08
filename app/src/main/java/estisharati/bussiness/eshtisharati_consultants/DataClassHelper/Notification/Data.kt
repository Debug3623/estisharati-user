package estisharati.bussiness.eshtisharati_consultants.DataClassHelper.Notification

data class Data(
    val body: String,
    val created_at: String,
    val details: ArrayList<Detail>?,
    val id: String,
    val title: String,
    val type: String
)