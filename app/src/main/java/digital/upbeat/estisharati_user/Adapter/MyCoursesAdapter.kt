package digital.upbeat.estisharati_user.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import digital.upbeat.estisharati_user.Fragment.Consultations
import digital.upbeat.estisharati_user.R
import digital.upbeat.estisharati_user.UI.MyConsultations
import digital.upbeat.estisharati_user.UI.MyCourses
import digital.upbeat.estisharati_user.ViewHolder.MyConsultationsViewHolder
import digital.upbeat.estisharati_user.ViewHolder.MyCoursesViewHolder

class MyCoursesAdapter(val context: Context, val myCourses: MyCourses, val arrayListStr: ArrayList<String>) : RecyclerView.Adapter<MyCoursesViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyCoursesViewHolder {
        val layoutView = LayoutInflater.from(context).inflate(R.layout.my_courses_item, parent, false)
        return MyCoursesViewHolder(layoutView)
    }

    override fun getItemCount(): Int {
        return arrayListStr.size
    }

    override fun onBindViewHolder(holder: MyCoursesViewHolder, position: Int) {
        when (position) {
            0 -> {
                holder.courses_name.text="Introduction to human resources tasks"
                holder.start_course.visibility = View.VISIBLE
                holder.add_review_layout.visibility = View.GONE
            }
            1 -> {
                holder.courses_name.text="How to become a lawyer"
                holder.start_course.visibility = View.GONE
                holder.add_review_layout.visibility = View.VISIBLE
            }
            2 -> {
                holder.courses_name.text="How to get a distinguished job?"
                holder.start_course.visibility = View.VISIBLE
                holder.add_review_layout.visibility = View.GONE
            }
            3 -> {
                holder.start_course.visibility = View.GONE
                holder.add_review_layout.visibility = View.VISIBLE
            }
        }
    }
}
