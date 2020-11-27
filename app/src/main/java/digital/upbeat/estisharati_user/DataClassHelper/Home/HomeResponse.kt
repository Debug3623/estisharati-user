package digital.upbeat.estisharati_user.DataClassHelper.Home

data class HomeResponse(
    val categories: ArrayList<Category>,
    val consultants: ArrayList<Consultant>,
    val courses: ArrayList<Course>,
    val notification_count: String,
    val slider: ArrayList<Slider>
)