package digital.upbeat.estisharati_user.DataClassHelper.CourseDetails

data class ResponseCourseDetails(
    val average_rating: AverageRating,
    val category: String,
    val category_id: Int,
    val chapters_count: Int,
    var comments: ArrayList<Comment>,
    val consultants: ArrayList<Consultant>,
    val course_duration: Double,
    val course_resources: ArrayList<CourseResource>,
    val description: String,
    val downloadable: Boolean,
    var favourite: Boolean,
    val id: Int,
    val image: String,
    val image_path: String,
    val name: String,
    val period: String,
    val preview_video: String,
    val price: Int,
    val rate: String,
    val status: Int,
    val video_path: String,
    val videos_count: Int
)