package digital.upbeat.estisharati_user.DataClassHelper.Search

data class Course(
    val category_id: Int,
    val id: String,
    val image_path: String,
    val name: String,
    val period: String,
    val price: Int,
    val rate: Double,
    val status: Int
)