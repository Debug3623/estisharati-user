package digital.upbeat.estisharati_user.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import digital.upbeat.estisharati_user.Fragment.Home
import digital.upbeat.estisharati_user.R
import digital.upbeat.estisharati_user.ViewHolder.ExpConsultationsViewHolder
import digital.upbeat.estisharati_user.ViewHolder.ExpCoursesViewHolder

class ExpCoursesAdapter(val context: Context, val home: Home,val arrayListStr: ArrayList<String>) : RecyclerView.Adapter<ExpCoursesViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpCoursesViewHolder {
        val layoutView = LayoutInflater.from(context).inflate(R.layout.exp_courses_item, parent, false)
        return ExpCoursesViewHolder(layoutView)
    }

    override fun getItemCount(): Int {
        return arrayListStr.size
    }

    override fun onBindViewHolder(holder: ExpCoursesViewHolder, position: Int) {
    }
}
