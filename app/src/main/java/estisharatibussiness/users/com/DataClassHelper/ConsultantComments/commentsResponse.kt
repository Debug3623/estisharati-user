package estisharatibussiness.users.com.DataClassHelper.ConsultantComments

import estisharatibussiness.users.com.DataClassHelper.ConsultantDetails.Comment

data class commentsResponse(
    val `data`: ArrayList<Comment>,
    val status: String,
    val message: String
)