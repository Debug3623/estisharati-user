package digital.upbeat.estisharati_consultant.DataClassHelper.RecentChat

import java.util.*
data class DataUserFireStore(val user_id: String, val fname: String, val lname: String, val email: String, val phone: String, val image: String, val fire_base_token: String, val user_type: String, var online_status: Boolean, val last_seen: Date, var availability: Boolean, var channel_unique_id: String,var blocked_user_ids:ArrayList<String>){
    constructor() : this("", "", "", "", "", "", "","",false,Date(),true,"", arrayListOf())

}