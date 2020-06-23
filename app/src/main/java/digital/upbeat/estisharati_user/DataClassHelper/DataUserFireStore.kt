package digital.upbeat.estisharati_user.DataClassHelper

import java.util.*
data class DataUserFireStore(val user_id: String, val fname: String, val lname: String, val email: String, val phone: String, val image: String, val fire_base_token: String, val user_type: String, val online_status: Boolean, val last_seen: Date=Date(), val availability: Boolean, val channel_unique_id: String){
    constructor() : this("", "", "", "", "", "", "","",false,Date(),true,"")

}