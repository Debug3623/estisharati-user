package digital.upbeat.estisharati_user.DataClassHelper.Consultant

data class Consultant(
    val course_id: Int,
    val id: Int,
    val laravel_through_key: Int,
    val rate: Int,
    val user: User,
    val user_id: Int
)