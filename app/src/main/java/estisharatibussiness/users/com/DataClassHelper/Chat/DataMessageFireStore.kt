package estisharatibussiness.users.com.DataClassHelper.Chat

import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

data class DataMessageFireStore(var message_id:String="",val sender_id: String, val receiver_id: String, val message_type: String, val message_content: String, val message_status: String, val message_other_type: String,  val send_time: Date, val communication_id: ArrayList<Int>, val inside_reply:HashMap<String,String> ){
    constructor(): this("","","","","","","", Date(),ArrayList<Int>(), hashMapOf())
}