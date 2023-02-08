package estisharati.bussiness.eshtisharati_consultants.DataClassHelper.Notification

data class NotificationsResponse(
    val status: String,
    val message: String,
    val `data`: ArrayList<Data>
)