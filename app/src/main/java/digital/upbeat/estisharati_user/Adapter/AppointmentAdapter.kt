package digital.upbeat.estisharati_user.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import digital.upbeat.estisharati_user.DataClassHelper.Appointment.Appointment
import digital.upbeat.estisharati_user.R
import digital.upbeat.estisharati_user.UI.MyAppointment
import digital.upbeat.estisharati_user.ViewHolder.AppointmentViewHolder

class AppointmentAdapter(val context: Context, val myAppointment: MyAppointment, var appointmentArrayList: ArrayList<Appointment>) : RecyclerView.Adapter<AppointmentViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        val layoutView = LayoutInflater.from(context).inflate(R.layout.appointment_item, parent, false)
        return AppointmentViewHolder(layoutView)
    }

    override fun getItemCount(): Int {
        return appointmentArrayList.size
    }

    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
        val appointmentItem = appointmentArrayList.get(position)
        if (myAppointment.helperMethods.findConsultantIsOnline(appointmentItem.consultant.id)) holder.online_status.visibility = View.VISIBLE else holder.online_status.visibility = View.GONE
        Glide.with(context).load(appointmentItem.consultant.image_path).apply(myAppointment.helperMethods.requestOption).into(holder.consultantImage)
        holder.consultantName.text = appointmentItem.consultant.name
        holder.consultantCategory.text = myAppointment.findCategoryID(appointmentItem.consultant.category_id)
        holder.appointmentDate.text = appointmentItem.date
        holder.appointmentTime.text = appointmentItem.time
        when (appointmentItem.status) {
            0 -> {
                holder.appointmentStatus.text = context.getString(R.string.pending)
                holder.appointmentStatus.setTextColor(ContextCompat.getColor(context, R.color.yellow))
            }
            1 -> {
                holder.appointmentStatus.text = context.getString(R.string.accepted)
                holder.appointmentStatus.setTextColor(ContextCompat.getColor(context, R.color.dark_green))
            }
            2 -> {
                holder.appointmentStatus.text = context.getString(R.string.rejected)
                holder.appointmentStatus.setTextColor(ContextCompat.getColor(context, R.color.red))
            }
        }
    }
}
