package estisharatibussiness.users.com.AdapterClasses

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import estisharatibussiness.users.com.DataClassHelperMehtods.Favourites.Course
import estisharatibussiness.users.com.R
import estisharatibussiness.users.com.UserInterfaces.ActivityCourseDetails
import estisharatibussiness.users.com.UserInterfaces.ActivityFavorites
import estisharatibussiness.users.com.PublicViewHolder.FavoriteCoursesViewHolder

class FavoriteCoursesAdapter(val context: Context, val activityFavorites: ActivityFavorites, val coursesArrayList: ArrayList<Course>) : RecyclerView.Adapter<FavoriteCoursesViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteCoursesViewHolder {
        val layoutView = LayoutInflater.from(context).inflate(R.layout.favorite_courses_item, parent, false)
        return FavoriteCoursesViewHolder(layoutView)
    }

    override fun getItemCount(): Int {
        return coursesArrayList.size
    }

    override fun onBindViewHolder(holder: FavoriteCoursesViewHolder, position: Int) {
        Glide.with(context).load(coursesArrayList.get(position).course.image_path).apply(activityFavorites.helperMethods.profileRequestOption).into(holder.courseImage)
        holder.courseName.text = coursesArrayList.get(position).course.name
        if (coursesArrayList.get(position).course.offerprice.equals("0")) {
            holder.coursePrice.text = context.getString(R.string.usd) + " " + coursesArrayList.get(position).course.price
        } else {
            holder.coursePrice.text = context.getString(R.string.usd) + " " + coursesArrayList.get(position).course.offerprice
        }
        holder.courseRating.text = coursesArrayList.get(position).course.rate
        holder.coursePeriod.text = coursesArrayList.get(position).course.period

        holder.parentLayout.setOnClickListener {
            val intent = Intent(context, ActivityCourseDetails::class.java)
            intent.putExtra("courseId", coursesArrayList.get(position).course.id)
            context.startActivity(intent)
        }
    }
}
