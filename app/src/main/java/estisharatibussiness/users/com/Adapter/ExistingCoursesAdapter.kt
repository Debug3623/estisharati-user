package estisharatibussiness.users.com.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import estisharatibussiness.users.com.DataClassHelper.Packages.Course
import estisharatibussiness.users.com.R
import estisharatibussiness.users.com.UserInterface.*
import estisharatibussiness.users.com.ViewHolder.ExistingCoursesViewHolder

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
        holder.coursePrice.text = context.getString(R.string.usd) + " " + coursesArrayList.get(position).price
        holder.courseLayout.setOnClickListener {
            val intent = Intent(context, ActivityCourseDetails::class.java)
            intent.putExtra("courseId", coursesArrayList.get(position).id)
            context.startActivity(intent)
        }
    }
}
