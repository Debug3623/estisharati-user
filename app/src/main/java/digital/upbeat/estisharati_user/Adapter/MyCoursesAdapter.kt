package digital.upbeat.estisharati_user.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import digital.upbeat.estisharati_user.DataClassHelper.MyCourse.Data
import digital.upbeat.estisharati_user.Fragment.Consultations
import digital.upbeat.estisharati_user.R
import digital.upbeat.estisharati_user.UI.CourseResource
import digital.upbeat.estisharati_user.UI.MyConsultations
import digital.upbeat.estisharati_user.UI.MyCourses
import digital.upbeat.estisharati_user.ViewHolder.MyConsultationsViewHolder
import digital.upbeat.estisharati_user.ViewHolder.MyCoursesViewHolder

class MyCoursesAdapter(val context: Context, val myCourses: MyCourses, var myCoursesArrayList: ArrayList<Data>) : RecyclerView.Adapter<MyCoursesViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyCoursesViewHolder {
        val layoutView = LayoutInflater.from(context).inflate(R.layout.my_courses_item, parent, false)
        return MyCoursesViewHolder(layoutView)
    }

    override fun getItemCount(): Int {
        return myCoursesArrayList.size
    }

    override fun onBindViewHolder(holder: MyCoursesViewHolder, position: Int) {
        val mycourseItem = myCoursesArrayList.get(position)
        holder.courses_name.text = mycourseItem.name
        Glide.with(context).load(mycourseItem.image_path).apply(myCourses.helperMethods.requestOption).into(holder.courseImage)
        holder.complete.text = "complete  ${mycourseItem.complete}%"
        if (mycourseItem.complete.equals("0")) {
            holder.start_course.visibility = View.VISIBLE
            holder.add_review_layout.visibility = View.GONE
        } else {
            holder.start_course.visibility = View.GONE
            holder.add_review_layout.visibility = View.VISIBLE
        }
        holder.reviewCourse.setOnClickListener {
            myCourses.showRatingPopup(mycourseItem)
        }
        holder.myCourseLayout.setOnClickListener {
            val intent = Intent(context, CourseResource::class.java)
            intent.putExtra("courseId", mycourseItem.id)
            context.startActivity(intent)
        }
    }
}
