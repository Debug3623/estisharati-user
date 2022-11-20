package estisharatibussiness.users.com.PublicViewHolder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.consultants_in_the_package_item.view.*

class ConsultantsInThePackageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val consultantImage=itemView.consultantImage
    val consultantName=itemView.consultantName
    val consultantLayout=itemView.consultantLayout
    val onlineStatus=itemView.onlineStatus
}