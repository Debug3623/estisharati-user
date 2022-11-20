package estisharatibussiness.users.com.PublicViewHolder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.consultations_item.view.*

class ConsultationsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val consul_name=itemView.consultantName
    val parentLayout=itemView.parentLayout
}