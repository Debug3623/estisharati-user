package digital.upbeat.estisharati_user.DataClassHelper.ConsultantComments

import digital.upbeat.estisharati_user.DataClassHelper.ConsultantDetails.Comment

data class commentsResponse(
    val `data`: ArrayList<Comment>,
    val status: String
)