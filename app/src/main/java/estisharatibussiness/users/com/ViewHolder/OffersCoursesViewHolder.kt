package estisharatibussiness.users.com.ViewHolder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.offers_courses_item.view.*

class OffersCoursesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val offersCourseName=itemView.offersCourseName
    val offersCourseImage = itemView.offersCourseImage
    val offersNewCoursePrice = itemView.offersNewCoursePrice
    val offersOldCoursePrice = itemView.offersOldCoursePrice
    val offersCourseRating = itemView.offersCourseRating
    val offersCourseEndDate = itemView.offersCourseEndDate
    val offersCoursePeriod = itemView.offersCoursePeriod
    val offersCoursesParentLayout = itemView.offersCoursesParentLayout
}