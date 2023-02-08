package estisharati.bussiness.eshtisharati_consultants.DataClassHelper.Login

import org.codehaus.jackson.annotate.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class DataUser(
    val id: String,
    val fname: String,
    val lname: String,
    val email: String,
    val phone: String,
    var image: String,
    val member_since: String,
    val access_token: String,
    val expired_days: String,
    val user_metas: DataUserMetas,
    val subscription: DataSubscription
) {
    constructor() : this(
        "", "", "", "", "", "", "", "","", DataUserMetas("", "", "","", "", "", "", ""),  DataSubscription("", "", "")
    )
}