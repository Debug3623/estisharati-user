package estisharatibussiness.users.com.DataClassHelperMehtods.CourseDetails

data class Reply(
    val comment: String,
    val course_id: String,
    val created_at: String,
    val id: String,
    val parent_id: String,
    val review: String,
    val user: User,
    val user_id: String
){
    constructor():this("","","","","","",User("","","","","",),"",)
}