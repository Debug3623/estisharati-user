package digital.upbeat.estisharati_user.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import digital.upbeat.estisharati_user.DataClassHelper.CourseDetails.CourseResource
import digital.upbeat.estisharati_user.Fragment.CourseContent
import digital.upbeat.estisharati_user.R
import digital.upbeat.estisharati_user.ViewHolder.CourseContentViewHolder

class CourseContentAdapter(val context: Context, val courseContent: CourseContent, val course_resources: ArrayList<CourseResource>) : RecyclerView.Adapter<CourseContentViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseContentViewHolder {
        val layoutView = LayoutInflater.from(context).inflate(R.layout.course_content_item, parent, false)
        return CourseContentViewHolder(layoutView)
    }

    override fun getItemCount(): Int {
        return course_resources.size
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
        holder.chapter_title.setOnClickListener {
            if (holder.child_layout.visibility == View.VISIBLE) {
                holder.child_layout.visibility = View.GONE
                holder.expand_icon.setImageResource(R.drawable.ic_plus_round)
            } else {
                holder.expand_icon.setImageResource(R.drawable.ic_minus_round)
                holder.child_layout.visibility = View.VISIBLE
            }
        }
        holder.chapter_title.text = course_resources.get(position).chapter_title
        holder.course_content_sub_recycler.setHasFixedSize(true)
        holder.course_content_sub_recycler.removeAllViews()
        holder.course_content_sub_recycler.layoutManager = LinearLayoutManager(context)
        holder.course_content_sub_recycler.adapter = CourseContentSubAdapter(context, courseContent, course_resources.get(position).lessons)
    }
}
