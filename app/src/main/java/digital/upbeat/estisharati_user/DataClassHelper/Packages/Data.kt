package digital.upbeat.estisharati_user.DataClassHelper.Packages

data class Data(
    val consultants: ArrayList<Consultant>,
    val courses: ArrayList<Course>,
    val effective_date: String,
    val features: Features,
    val id: Int,
    val name: String,
    val period: String,
    val price: String,
    val status: Int,
    val tag_id: Int,
    val type: String
)