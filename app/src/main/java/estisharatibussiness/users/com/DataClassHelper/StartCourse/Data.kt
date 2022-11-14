package estisharatibussiness.users.com.DataClassHelper.StartCourse

data class Data(
    val category_id: Int,
    val course_resources: ArrayList<CourseResource>,
    val description: String,
    val documents: ArrayList<Document>,
    val id: Int,
    val image: String,
    val image_path: String,
    val name: String,
    val preview_video: String,
    val video_path: String,
    var downloadable: Boolean,
    val videos: ArrayList<Video>
)