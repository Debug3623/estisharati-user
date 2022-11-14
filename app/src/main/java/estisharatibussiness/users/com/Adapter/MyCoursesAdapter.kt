package estisharatibussiness.users.com.Adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import estisharatibussiness.users.com.DataClassHelper.MyCourse.Data
import estisharatibussiness.users.com.R
import estisharatibussiness.users.com.UserInterface.ActivityCourseResource
import estisharatibussiness.users.com.UserInterface.ActivityMyCourses
import estisharatibussiness.users.com.ViewHolder.MyCoursesViewHolder

class MyCoursesAdapter(val context: Context, val activityMyCourses: ActivityMyCourses, var myCoursesArrayList: ArrayList<Data>) : RecyclerView.Adapter<MyCoursesViewHolder>() {
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
        Glide.with(context).load(mycourseItem.image_path).apply(activityMyCourses.helperMethods.requestOption).into(holder.courseImage)
        holder.complete.text = "${context.getString(R.string.complete)}  ${mycourseItem.complete}%"
        if (mycourseItem.complete.equals("0")) {
            holder.start_course.visibility = View.VISIBLE
            holder.add_review_layout.visibility = View.GONE
        } else {
            holder.start_course.visibility = View.GONE
            holder.add_review_layout.visibility = View.VISIBLE
        }
        holder.reviewCourse.setOnClickListener {
            activityMyCourses.showRatingPopup(mycourseItem)
        }
        holder.myCourseLayout.setOnClickListener {
            val intent = Intent(context, ActivityCourseResource::class.java)
            intent.putExtra("courseId", mycourseItem.id)
            Log.d("courseId==", mycourseItem.id)
            context.startActivity(intent)
        }
    }
}
