package digital.upbeat.estisharati_user.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import digital.upbeat.estisharati_user.DataClassHelper.Favourites.Course
import digital.upbeat.estisharati_user.R
import digital.upbeat.estisharati_user.UI.CourseDetails
import digital.upbeat.estisharati_user.UI.Favorites
import digital.upbeat.estisharati_user.ViewHolder.FavoriteCoursesViewHolder

class FavoriteCoursesAdapter(val context: Context, val favorites: Favorites, val coursesArrayList: ArrayList<Course>) : RecyclerView.Adapter<FavoriteCoursesViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteCoursesViewHolder {
        val layoutView = LayoutInflater.from(context).inflate(R.layout.favorite_courses_item, parent, false)
        return FavoriteCoursesViewHolder(layoutView)
    }

    override fun getItemCount(): Int {
        return coursesArrayList.size
    }

    override fun onBindViewHolder(holder: FavoriteCoursesViewHolder, position: Int) {
        Glide.with(context).load(coursesArrayList.get(position).course.image_path).apply(favorites.helperMethods.profileRequestOption).into(holder.courseImage)
        holder.courseName.text = coursesArrayList.get(position).course.name
        if (coursesArrayList.get(position).course.offerprice.equals("0")) {
            holder.coursePrice.text = context.getString(R.string.usd) + " " + coursesArrayList.get(position).course.price
        } else {
            holder.coursePrice.text = context.getString(R.string.usd) + " " + coursesArrayList.get(position).course.offerprice
        }
        holder.courseRating.text = coursesArrayList.get(position).course.rate
        holder.coursePeriod.text = coursesArrayList.get(position).course.period

        holder.parentLayout.setOnClickListener {
            val intent = Intent(context, CourseDetails::class.java)
            intent.putExtra("courseId", coursesArrayList.get(position).course.id)
            context.startActivity(intent)
        }
    }
}
