package estisharatibussiness.users.com.Adapter

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import estisharatibussiness.users.com.DataClassHelper.Offers.Course
import estisharatibussiness.users.com.R
import estisharatibussiness.users.com.UserInterface.ActivityCourseDetails
import estisharatibussiness.users.com.UserInterface.ActivityOffers
import estisharatibussiness.users.com.ViewHolder.OffersCoursesViewHolder

class OffersCoursesAdapter(val context: Context, val activityOffers: ActivityOffers, val coursesArrayList: ArrayList<Course>) : RecyclerView.Adapter<OffersCoursesViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OffersCoursesViewHolder {
        val layoutView = LayoutInflater.from(context).inflate(R.layout.offers_courses_item, parent, false)
        return OffersCoursesViewHolder(layoutView)
    }

    override fun getItemCount(): Int {
        return coursesArrayList.size
    }

    override fun onBindViewHolder(holder: OffersCoursesViewHolder, position: Int) {
        Glide.with(context).load(coursesArrayList.get(position).course.image_path).apply(activityOffers.helperMethods.profileRequestOption).into(holder.offersCourseImage)
        holder.offersCourseName.text = coursesArrayList.get(position).course.name
        holder.offersNewCoursePrice.text = context.getString(R.string.usd) + " " + coursesArrayList.get(position).offerprice
        holder.offersOldCoursePrice.text = coursesArrayList.get(position).course.price
        holder.offersCourseRating.text = coursesArrayList.get(position).course.rate
        holder.offersCourseEndDate.text = coursesArrayList.get(position).enddate
        holder.offersCoursePeriod.text = coursesArrayList.get(position).course.period
        holder.offersOldCoursePrice.paintFlags = holder.offersOldCoursePrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG


        holder.offersCoursesParentLayout.setOnClickListener {
            val intent = Intent(context, ActivityCourseDetails::class.java)
            intent.putExtra("courseId", coursesArrayList.get(position).course.id)
            context.startActivity(intent)
        }
    }
}
