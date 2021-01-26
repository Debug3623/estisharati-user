package digital.upbeat.estisharati_consultant.DataClassHelper.Appointment

data class Appointment(
    val category_id: String,
    val consultant_id: String,
    val date: String,
    val id: String,
    val status: Int,
    val time: String,
    val user: User,
    val user_id: Int
)