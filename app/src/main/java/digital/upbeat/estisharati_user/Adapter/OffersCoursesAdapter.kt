package digital.upbeat.estisharati_user.Adapter

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import digital.upbeat.estisharati_user.DataClassHelper.Offers.Consultant
import digital.upbeat.estisharati_user.DataClassHelper.Offers.Course
import digital.upbeat.estisharati_user.R
import digital.upbeat.estisharati_user.UI.CourseDetails
import digital.upbeat.estisharati_user.UI.Favorites
import digital.upbeat.estisharati_user.UI.Offers
import digital.upbeat.estisharati_user.ViewHolder.FavoriteCoursesViewHolder
import digital.upbeat.estisharati_user.ViewHolder.OffersConsultantsViewHolder
import digital.upbeat.estisharati_user.ViewHolder.OffersCoursesViewHolder

class OffersCoursesAdapter(val context: Context, val offers: Offers, val coursesArrayList: ArrayList<Course>) : RecyclerView.Adapter<OffersCoursesViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OffersCoursesViewHolder {
        val layoutView = LayoutInflater.from(context).inflate(R.layout.offers_courses_item, parent, false)
        return OffersCoursesViewHolder(layoutView)
    }

    override fun getItemCount(): Int {
        return coursesArrayList.size
    }

    override fun onBindViewHolder(holder: OffersCoursesViewHolder, position: Int) {
        Glide.with(context).load(coursesArrayList.get(position).course.image_path).apply(offers.helperMethods.profileRequestOption).into(holder.offersCourseImage)
        holder.offersCourseName.text = coursesArrayList.get(position).course.name
        holder.offersNewCoursePrice.text = context.getString(R.string.usd) + " " + coursesArrayList.get(position).offerprice
        holder.offersOldCoursePrice.text = coursesArrayList.get(position).course.price
        holder.offersCourseRating.text = coursesArrayList.get(position).course.rate
        holder.offersCourseEndDate.text = coursesArrayList.get(position).enddate
        holder.offersCoursePeriod.text = coursesArrayList.get(position).course.period
        holder.offersOldCoursePrice.setPaintFlags( holder.offersOldCoursePrice.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)


        holder.offersCoursesParentLayout.setOnClickListener {
            val intent = Intent(context, CourseDetails::class.java)
            intent.putExtra("courseId", coursesArrayList.get(position).course.id)
            context.startActivity(intent)
        }
    }
}
