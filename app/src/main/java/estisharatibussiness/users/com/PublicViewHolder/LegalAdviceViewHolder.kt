package estisharatibussiness.users.com.PublicViewHolder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.legal_advice_item.view.*

class LegalAdviceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val parentLayout=itemView.parentLayout
    val consultantImage=itemView.consultantImage
    val consultantName=itemView.consultantName
    val consultantJobTitle=itemView.consultantJobTitle
    val consultantPrice=itemView.consultantPrice
    val consultantRate=itemView.consultantRate
    val online_status=itemView.online_status
}