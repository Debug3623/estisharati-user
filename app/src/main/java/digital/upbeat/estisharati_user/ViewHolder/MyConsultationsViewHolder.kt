package digital.upbeat.estisharati_user.ViewHolder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.my_consultations_item.view.*

class MyConsultationsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val consul_name = itemView.consul_name
    val consul_profession = itemView.consul_profession
    val contact_image = itemView.contact_image
    val contact_type = itemView.contact_type
}