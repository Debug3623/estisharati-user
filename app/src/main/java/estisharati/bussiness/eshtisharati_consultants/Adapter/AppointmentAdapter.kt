package estisharati.bussiness.eshtisharati_consultants.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import estisharati.bussiness.eshtisharati_consultants.DataClassHelper.Appointment.Appointment
import estisharati.bussiness.eshtisharati_consultants.R
import estisharati.bussiness.eshtisharati_consultants.UI.MyAppointment
import estisharati.bussiness.eshtisharati_consultants.ViewHolder.AppointmentViewHolder

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
        Glide.with(context).load(appointmentItem.user.image_path).apply(myAppointment.helperMethods.requestOption).into(holder.consultantImage)
        holder.consultantName.text = appointmentItem.user.name
        holder.consultationCategory.text = myAppointment.findCategoryID(appointmentItem.category_id)
        holder.appointmentDate.text = appointmentItem.date
        holder.appointmentTime.text = appointmentItem.time
        when (appointmentItem.status) {
            0 -> {
                holder.appointmentStatus.text = context.getString(R.string.pending)
                holder.appointmentStatus.setTextColor(ContextCompat.getColor(context, R.color.yellow))
                holder.actionLayout.visibility = View.VISIBLE
                holder.appointmentStatus.visibility = View.GONE
            }
            1 -> {
                holder.appointmentStatus.text = context.getString(R.string.accepted)
                holder.appointmentStatus.setTextColor(ContextCompat.getColor(context, R.color.dark_green))
                holder.actionLayout.visibility = View.GONE
                holder.appointmentStatus.visibility = View.VISIBLE
            }
            2 -> {
                holder.appointmentStatus.text = context.getString(R.string.rejected)
                holder.appointmentStatus.setTextColor(ContextCompat.getColor(context, R.color.red))
                holder.actionLayout.visibility = View.GONE
                holder.appointmentStatus.visibility = View.VISIBLE
            }
        }

        holder.acceptAppointment.setOnClickListener {
            myAppointment.appointmentAction(position, "1", context.getString(R.string.are_you_sure_do_you_want_accept_consultation_on) + " " + myAppointment.findCategoryID(appointmentItem.category_id) + " " +context.getString(R.string.at) + " " + appointmentItem.date + " " + appointmentItem.time)
        }
        holder.rejectAppointment.setOnClickListener {
            myAppointment.appointmentAction(position, "2", context.getString(R.string.are_you_sure_do_you_want_reject_consultation_on) + " " + myAppointment.findCategoryID(appointmentItem.category_id) + " " + context.getString(R.string.at) + " " + appointmentItem.date + " " + appointmentItem.time)
        }
    }
}
