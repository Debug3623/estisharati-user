package estisharatibussiness.users.com.DataClassHelperMehtods.ConsultantComments

import estisharatibussiness.users.com.DataClassHelperMehtods.ConsultantDetails.Comment

data class commentsResponse(
    val `data`: ArrayList<Comment>,
    val status: String,
    val message: String
)