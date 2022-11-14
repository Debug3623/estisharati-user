package estisharatibussiness.users.com.DataClassHelper.Login

import org.codehaus.jackson.annotate.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class DataUser(val id: String, val fname: String, val lname: String, val email: String, val phone: String, var image: String, val member_since: String, val user_metas: DataUserMetas, val access_token: String, val expired_days:String,val subscription : DataSubscription,val google_login:Boolean) {
    constructor() : this("", "", "", "", "", "", "", DataUserMetas("","","" ,""), "","", DataSubscription("","",""),false)
}