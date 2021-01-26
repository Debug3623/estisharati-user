package digital.upbeat.estisharati_consultant.DataClassHelper.MySubsribers

data class MySubscriberResponse(
    val status: String,
    val message: String,
    val `data`: ArrayList<Data>,
    val course_count: String,
    val notification_count: String,
    val consultation_count: String,
    val message_types: ArrayList<MessageTypes>,
)