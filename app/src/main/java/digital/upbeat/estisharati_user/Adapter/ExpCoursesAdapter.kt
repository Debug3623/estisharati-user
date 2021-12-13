package digital.upbeat.estisharati_user.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import digital.upbeat.estisharati_user.DataClassHelper.Home.Course
import digital.upbeat.estisharati_user.Fragment.Home
import digital.upbeat.estisharati_user.R
import digital.upbeat.estisharati_user.UI.CourseDetails
import digital.upbeat.estisharati_user.ViewHolder.ExpCoursesViewHolder
import kotlinx.android.synthetic.main.activity_course_details.*

class ExpCoursesAdapter(val context: Context, val home: Home, val coursesArrayList: ArrayList<Course>) : RecyclerView.Adapter<ExpCoursesViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpCoursesViewHolder {
        val layoutView = LayoutInflater.from(context).inflate(R.layout.exp_courses_item, parent, false)
        return ExpCoursesViewHolder(layoutView)
    }

    override fun getItemCount(): Int {
        return coursesArrayList.size
    }

    override fun onBindViewHolder(holder: ExpCoursesViewHolder, position: Int) {
        Glide.with(context).load(coursesArrayList.get(position).image_path).apply(home.helperMethods.requestOption).into(holder.courseImage)
        if (coursesArrayList.get(position).offerprice.equals("0")) {
            holder.coursePrice.text = context.resources.getString(R.string.usd) + " " + coursesArrayList.get(position).price
        } else {
            holder.coursePrice.text = context.resources.getString(R.string.usd) + " " + coursesArrayList.get(position).offerprice
        }
        holder.courseName.text = coursesArrayList.get(position).name
        holder.parentLayout.setOnClickListener {
            val intent = Intent(context, CourseDetails::class.java)
            intent.putExtra("courseId", coursesArrayList.get(position).id)
            context.startActivity(intent)
        }
    }
}
