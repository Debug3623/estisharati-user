package estisharatibussiness.users.com.DataClassHelperMehtods.Login

import org.codehaus.jackson.annotate.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class DataUserMetas(val city: String,  val phone_code: String,val country:String,val fire_base_token:String) {
    constructor() : this("", "","","")
}