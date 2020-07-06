package digital.upbeat.estisharati_user.DataClassHelper

import java.util.*
import kotlin.collections.ArrayList

data class DataMessageFireStore(val sender_id: String, val receiver_id: String, val message_type: String, val message_content: String, val message_status: String,  val send_time: Date, val communication_id: ArrayList<Int>){
    constructor(): this("","","","","", Date(),ArrayList<Int>())
}