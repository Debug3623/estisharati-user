package estisharatibussiness.users.com.DataClassHelper.ChatGroupMessage


import java.util.*

data class GroupMessageFireStore(var message_id:String="",val sender_id: String, val message_type: String, val message_content: String,  val message_other_type: String,  val send_time: Date, val inside_reply:HashMap<String,String> ){
    constructor(): this("","","","","", Date(), hashMapOf())
}