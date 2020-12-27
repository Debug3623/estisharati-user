package digital.upbeat.estisharati_user.DataClassHelper.CourseDetails


data class Comment(val comment: String, val course_id: String, val created_at: String, val id: String, val parent_id: String, val replies: ArrayList<Reply>, val review: String, val user: User, val user_id: String) {
    constructor() : this("", "", "", "", "", arrayListOf<Reply>(), "", User("", "", "", "", ""), "")
}