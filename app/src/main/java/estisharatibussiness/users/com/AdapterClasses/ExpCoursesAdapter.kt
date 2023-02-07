package estisharatibussiness.users.com.AdapterClasses

import android.content.Context
import android.content.Intent
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import estisharatibussiness.users.com.DataClassHelperMehtods.Home.Course
import estisharatibussiness.users.com.FragmentClasses.Home
import estisharatibussiness.users.com.Helper.SharedPreferencesHelper
import estisharatibussiness.users.com.R
import estisharatibussiness.users.com.UserInterfaces.ActivityCourseDetails
import estisharatibussiness.users.com.PublicViewHolder.ExpCoursesViewHolder

class ExpCoursesAdapter(val context: Context, val home: Home, val coursesArrayList: ArrayList<Course>) : RecyclerView.Adapter<ExpCoursesViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpCoursesViewHolder {
        val layoutView = LayoutInflater.from(context).inflate(R.layout.exp_courses_item, parent, false)
        return ExpCoursesViewHolder(layoutView)
    }

    override fun getItemCount(): Int {
        return coursesArrayList.size
    }

    override fun onBindViewHolder(holder: ExpCoursesViewHolder, position: Int) {
//        var preferencesHelper: SharedPreferencesHelper = SharedPreferencesHelper(context)
        Glide.with(context).load(coursesArrayList.get(position).image_path).apply(home.helperMethods.requestOption).into(holder.courseImage)
        if (coursesArrayList.get(position).offerprice.equals("0")) {
               holder.coursePrice.text = context.resources.getString(R.string.usd) + " " + coursesArrayList.get(position).price
           }

        else {
            holder.coursePrice.text = context.resources.getString(R.string.usd) + " " + coursesArrayList.get(position).offerprice
        }
        holder.courseName.text = coursesArrayList.get(position).name
        holder.parentLayout.setOnClickListener {
            val intent = Intent(context, ActivityCourseDetails::class.java)
            intent.putExtra("courseId", coursesArrayList.get(position).id)
            context.startActivity(intent)
        }
    }
}
