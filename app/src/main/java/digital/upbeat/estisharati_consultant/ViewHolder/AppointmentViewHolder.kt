package digital.upbeat.estisharati_consultant.ViewHolder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.appointment_item.view.*

class AppointmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
val consultantImage=itemView.consultantImage
val consultantName=itemView.userName
val consultationCategory=itemView.consultationCategory
val appointmentDate=itemView.appointmentDate
val appointmentTime=itemView.appointmentTime
val appointmentStatus=itemView.appointmentStatus
val acceptAppointment=itemView.acceptAppointment
val rejectAppointment=itemView.rejectAppointment
val actionLayout=itemView.actionLayout
}