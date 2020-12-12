package digital.upbeat.estisharati_user.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import digital.upbeat.estisharati_user.DataClassHelper.OnlineCourses.DataOnlineCourses
import digital.upbeat.estisharati_user.Fragment.Consultations
import digital.upbeat.estisharati_user.R
import digital.upbeat.estisharati_user.UI.CourseDetails
import digital.upbeat.estisharati_user.UI.MyConsultations
import digital.upbeat.estisharati_user.UI.MyCourses
import digital.upbeat.estisharati_user.UI.OnlineCourses
import digital.upbeat.estisharati_user.ViewHolder.MyConsultationsViewHolder
import digital.upbeat.estisharati_user.ViewHolder.MyCoursesViewHolder
import digital.upbeat.estisharati_user.ViewHolder.OnlineCoursesViewHolder

class OnlineCoursesAdapter(val context: Context, val onlineCourses: OnlineCourses, val onlineCoursesArrayList: ArrayList<DataOnlineCourses>) : RecyclerView.Adapter<OnlineCoursesViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnlineCoursesViewHolder {
        val layoutView = LayoutInflater.from(context).inflate(R.layout.online_courses_item, parent, false)
        return OnlineCoursesViewHolder(layoutView)
    }

    override fun getItemCount(): Int {
        return onlineCoursesArrayList.size
    }

    override fun onBindViewHolder(holder: OnlineCoursesViewHolder, position: Int) {
        val dataOnlineCourses = onlineCoursesArrayList.get(position)
        Glide.with(context).load(dataOnlineCourses.image_path).apply(onlineCourses.helperMethods.requestOption).into(holder.courseImage)
        holder.courseName.text = dataOnlineCourses.name
        if (dataOnlineCourses.offerprice.equals("0")) {
            holder.coursePrice.text = context.getString(R.string.aed) + " " + dataOnlineCourses.price
        } else {
            holder.coursePrice.text = context.getString(R.string.aed) + " " + dataOnlineCourses.offerprice
        }
        holder.courseRating.text = dataOnlineCourses.rate
        holder.coursePeriod.text = dataOnlineCourses.period
        holder.parentLayout.setOnClickListener {
            val intent = Intent(context, CourseDetails::class.java)
            intent.putExtra("courseId", dataOnlineCourses.id)
            context.startActivity(intent)
        }
    }
}
