package digital.upbeat.estisharati_user.DataClassHelper

import org.codehaus.jackson.annotate.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class DataUser(val id: String, val fname: String, val lname: String, val email: String, val phone: String, val image: String, val member_since: String, val user_metas: DataUserMetas, val access_token: String,val subscription : DataSubscription) {
    constructor() : this("", "", "", "", "", "", "", DataUserMetas("", ""), "",DataSubscription("","",""))
}