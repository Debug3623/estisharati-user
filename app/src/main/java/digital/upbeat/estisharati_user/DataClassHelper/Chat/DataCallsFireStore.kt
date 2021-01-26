package digital.upbeat.estisharati_user.DataClassHelper.Chat

import java.util.*

data class DataCallsFireStore (val channel_unique_id:String,val caller_id : String,val receiver_id:String,val call_type:String,val call_status:String,val ringing_duration:String){
    constructor() : this("", "", "", "", "", "")


}