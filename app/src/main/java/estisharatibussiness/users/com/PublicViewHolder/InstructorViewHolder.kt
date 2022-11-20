package estisharatibussiness.users.com.PublicViewHolder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.instructor_item.view.*

class InstructorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val consultantImage=itemView.consultantImage
    val consultantName=itemView.consultantName
    val consultantRate=itemView.consultantRate
    val courseCount=itemView.courseCount
    val qualificationDetails=itemView.qualificationDetails
}