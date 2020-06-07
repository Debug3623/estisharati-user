package digital.upbeat.estisharati_user.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import digital.upbeat.estisharati_user.Fragment.Consultations
import digital.upbeat.estisharati_user.Fragment.CourseContent
import digital.upbeat.estisharati_user.R
import digital.upbeat.estisharati_user.UI.MyConsultations
import digital.upbeat.estisharati_user.UI.MyCourses
import digital.upbeat.estisharati_user.UI.OnlineCourses
import digital.upbeat.estisharati_user.ViewHolder.CourseContentViewHolder
import digital.upbeat.estisharati_user.ViewHolder.MyConsultationsViewHolder
import digital.upbeat.estisharati_user.ViewHolder.MyCoursesViewHolder
import digital.upbeat.estisharati_user.ViewHolder.OnlineCoursesViewHolder
import kotlinx.android.synthetic.main.fragment_comments.*

class CourseContentAdapter(val context: Context, val courseContent: CourseContent, val arrayListStr: ArrayList<String>) : RecyclerView.Adapter<CourseContentViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseContentViewHolder {
        val layoutView = LayoutInflater.from(context).inflate(R.layout.course_content_item, parent, false)
        return CourseContentViewHolder(layoutView)
    }

    override fun getItemCount(): Int {
        return arrayListStr.size
    }

    override fun onBindViewHolder(holder: CourseContentViewHolder, position: Int) {
        holder.child_layout.visibility = View.GONE
        holder.expand_icon.setImageResource(R.drawable.ic_plus_round)
        holder.expand_icon.setOnClickListener {
            if (holder.child_layout.visibility == View.VISIBLE) {
                holder.child_layout.visibility = View.GONE
                holder.expand_icon.setImageResource(R.drawable.ic_plus_round)
            } else {
                holder.expand_icon.setImageResource(R.drawable.ic_minus_round)
                holder.child_layout.visibility = View.VISIBLE
            }
        }
        holder.main_text.setOnClickListener {
            if (holder.child_layout.visibility == View.VISIBLE) {
                holder.child_layout.visibility = View.GONE
                holder.expand_icon.setImageResource(R.drawable.ic_plus_round)
            } else {
                holder.expand_icon.setImageResource(R.drawable.ic_minus_round)
                holder.child_layout.visibility = View.VISIBLE
            }
        }

        val arrayList: ArrayList<String> = arrayListOf()
        arrayList.add("Marketing advice")
        arrayList.add("Legal advice")
        arrayList.add("Administration and business")


        holder.course_content_sub_recycler.setHasFixedSize(true)
        holder.course_content_sub_recycler.removeAllViews()
        holder.course_content_sub_recycler.layoutManager = LinearLayoutManager(context)
        holder.course_content_sub_recycler.adapter = CourseContentSubAdapter(context, courseContent, arrayList)

    }
}
