package estisharatibussiness.users.com.PublicViewHolder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.offers_consultants_item.view.*

class OffersConsultantsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val offersConsultantName=itemView.offersConsultantName
    val offersConsultantEndDate = itemView.offersConsultantEndDate
    val offersConsultantJobTitle = itemView.offersConsultantJobTitle
    val offersConsultantRate = itemView.offersConsultantRate
    val offersOldConsultantPrice = itemView.offersOldConsultantPrice
    val offersNewConsultantPrice = itemView.offersNewConsultantPrice
    val offersConsultantImage = itemView.offersConsultantImage
    val offersConsultantParentLayout = itemView.offersConsultantParentLayout
    val onlineStatus = itemView.onlineStatus

}