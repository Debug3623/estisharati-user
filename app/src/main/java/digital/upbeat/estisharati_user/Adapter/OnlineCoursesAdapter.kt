package digital.upbeat.estisharati_user.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import digital.upbeat.estisharati_user.Fragment.Consultations
import digital.upbeat.estisharati_user.R
import digital.upbeat.estisharati_user.UI.MyConsultations
import digital.upbeat.estisharati_user.UI.MyCourses
import digital.upbeat.estisharati_user.UI.OnlineCourses
import digital.upbeat.estisharati_user.ViewHolder.MyConsultationsViewHolder
import digital.upbeat.estisharati_user.ViewHolder.MyCoursesViewHolder
import digital.upbeat.estisharati_user.ViewHolder.OnlineCoursesViewHolder

class OnlineCoursesAdapter(val context: Context, val onlineCourses: OnlineCourses, val arrayListStr: ArrayList<String>) : RecyclerView.Adapter<OnlineCoursesViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnlineCoursesViewHolder {
        val layoutView = LayoutInflater.from(context).inflate(R.layout.online_courses_item, parent, false)
        return OnlineCoursesViewHolder(layoutView)
    }

    override fun getItemCount(): Int {
        return arrayListStr.size
    }

    override fun onBindViewHolder(holder: OnlineCoursesViewHolder, position: Int) {

    }
}
