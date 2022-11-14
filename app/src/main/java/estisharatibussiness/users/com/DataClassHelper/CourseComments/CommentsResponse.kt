package estisharatibussiness.users.com.DataClassHelper.CourseComments

import estisharatibussiness.users.com.DataClassHelper.CourseDetails.Comment

data class CommentsResponse(
    val `data`: ArrayList<Comment>,
    val status: String,
    val message: String
)