package digital.upbeat.estisharati_user.ViewHolder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.package_item.view.*

class PackageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val packageName = itemView.packageName
    val packagePrice = itemView.packagePrice
    val packagePeriod = itemView.packagePeriod
    val videoLayout = itemView.videoLayout
    val videoHourse = itemView.videoHourse
    val voiceLayout = itemView.voiceLayout
    val voiceHourse = itemView.voiceHourse
    val writtenLayout = itemView.writtenLayout
    val writtenHourse = itemView.writtenHourse
    val showExisitingCourses = itemView.showExisitingCourses
    val showConsultantsInThePackage = itemView.showConsultantsInThePackage
    val consultantImage1 = itemView.consultantImage1
    val consultantImage2 = itemView.consultantImage2
    val consultantImage3 = itemView.consultantImage3
    val consultantImage4 = itemView.consultantImage4
    val consultantImage5 = itemView.consultantImage5

}