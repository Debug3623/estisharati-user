package estisharatibussiness.users.com.PublicViewHolder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.my_consultations_item.view.*

class MyConsultationsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val myConsultationsLayout = itemView.myConsultationsLayout
    val consultantName = itemView.consultantName
    val consultantCategory = itemView.consultantCategory
    val consultantImage = itemView.consultantImage
    val chatOption = itemView.chatOption
    val voiceOption = itemView.voiceOption
    val videoOption = itemView.videoOption
    val rateNow = itemView.rateNow
    val online_status = itemView.online_status
}