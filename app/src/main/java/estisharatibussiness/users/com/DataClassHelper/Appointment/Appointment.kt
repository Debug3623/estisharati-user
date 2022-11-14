package estisharatibussiness.users.com.DataClassHelper.Appointment

data class Appointment(
    val consultant: Consultant,
    val consultant_id: String,
    val date: String,
    val id: String,
    val status: Int,
    val time: String,
    val user_id: Int
)