package digital.upbeat.estisharati_user.DataClassHelper.Favourites

data class CourseX(
    val description: String,
    val id: String,
    val image: String,
    val image_path: String,
    val name: String,
    val period: String,
    val price: String,
    val offerprice: String?,
    val rate: String,
    val translations: List<Translation>,
    val video_path: String
)