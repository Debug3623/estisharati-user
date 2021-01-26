package digital.upbeat.estisharati_user.ViewHolder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.appointment_item.view.*

class AppointmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
val consultantImage=itemView.consultantImage
val online_status=itemView.online_status
val consultantName=itemView.consultantName
val consultantCategory=itemView.consultantCategory
val appointmentDate=itemView.appointmentDate
val appointmentTime=itemView.appointmentTime
val appointmentStatus=itemView.appointmentStatus
}