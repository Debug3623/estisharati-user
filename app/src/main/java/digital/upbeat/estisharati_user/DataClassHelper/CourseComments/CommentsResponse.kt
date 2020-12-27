package digital.upbeat.estisharati_user.DataClassHelper.CourseComments

import digital.upbeat.estisharati_user.DataClassHelper.CourseDetails.Comment

data class CommentsResponse(
    val `data`: ArrayList<Comment>,
    val status: String,
    val message: String
)