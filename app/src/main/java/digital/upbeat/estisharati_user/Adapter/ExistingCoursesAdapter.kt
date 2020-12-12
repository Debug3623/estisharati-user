package digital.upbeat.estisharati_user.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import digital.upbeat.estisharati_user.DataClassHelper.Packages.Course
import digital.upbeat.estisharati_user.Fragment.Consultations
import digital.upbeat.estisharati_user.R
import digital.upbeat.estisharati_user.UI.ExistingCourses
import digital.upbeat.estisharati_user.UI.MyConsultations
import digital.upbeat.estisharati_user.UI.MyCourses
import digital.upbeat.estisharati_user.UI.OnlineCourses
import digital.upbeat.estisharati_user.ViewHolder.ExistingCoursesViewHolder
import digital.upbeat.estisharati_user.ViewHolder.MyConsultationsViewHolder
import digital.upbeat.estisharati_user.ViewHolder.MyCoursesViewHolder
import digital.upbeat.estisharati_user.ViewHolder.OnlineCoursesViewHolder

class ExistingCoursesAdapter(val context: Context, val existingCourses: ExistingCourses, val coursesArrayList: ArrayList<Course>) : RecyclerView.Adapter<ExistingCoursesViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExistingCoursesViewHolder {
        val layoutView = LayoutInflater.from(context).inflate(R.layout.existing_courses_item, parent, false)
        return ExistingCoursesViewHolder(layoutView)
    }

    override fun getItemCount(): Int {
        return coursesArrayList.size
    }

    override fun onBindViewHolder(holder: ExistingCoursesViewHolder, position: Int) {
        Glide.with(context).load(coursesArrayList.get(position).image_path).apply(existingCourses.helperMethods.requestOption).into(holder.courseImage)
        holder.courseName.text = coursesArrayList.get(position).name
        holder.courseRating.text = coursesArrayList.get(position).rate
        holder.coursePrice.text = context.getString(R.string.aed) + " " + coursesArrayList.get(position).price
    }
}
