package estisharatibussiness.users.com.DataClassHelperMehtods.CourseComments

import estisharatibussiness.users.com.DataClassHelperMehtods.CourseDetails.Comment

data class CommentsResponse(
    val `data`: ArrayList<Comment>,
    val status: String,
    val message: String
)