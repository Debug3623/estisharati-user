package estisharatibussiness.users.com.AdapterClasses

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import estisharatibussiness.users.com.DataClassHelperMehtods.CourseDetails.Consultant
import estisharatibussiness.users.com.FragmentClasses.Instructor
import estisharatibussiness.users.com.R
import estisharatibussiness.users.com.PublicViewHolder.InstructorViewHolder

class InstructorAdapter(val context: Context, val instructor: Instructor, val consultants: ArrayList<Consultant>) : RecyclerView.Adapter<InstructorViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InstructorViewHolder {
        val layoutView = LayoutInflater.from(context).inflate(R.layout.instructor_item, parent, false)
        return InstructorViewHolder(layoutView)
    }

    override fun getItemCount(): Int {
        return consultants.size
    }

    override fun onBindViewHolder(holder: InstructorViewHolder, position: Int) {
        holder.consultantName.text = consultants.get(position).user.name
        holder.consultantRate.text = consultants.get(position).user.rate
        holder.courseCount.text =context.getString(R.string.total_course)+ consultants.get(position).user.course_count
        if (consultants.get(position).user.qualification.size > 0) {
            holder.qualificationDetails.text = consultants.get(position).user.qualification.get(0).attr_value
            holder.qualificationDetails.visibility = View.VISIBLE
        } else {
            holder.qualificationDetails.visibility = View.GONE
        }
        Glide.with(context).load(consultants.get(position).user.image_path).apply(instructor.helperMethods.profileRequestOption).into(holder.consultantImage)
    }
}
