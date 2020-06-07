package digital.upbeat.estisharati_user.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import digital.upbeat.estisharati_user.Fragment.Instructor
import digital.upbeat.estisharati_user.R
import digital.upbeat.estisharati_user.UI.LegalAdvice
import digital.upbeat.estisharati_user.ViewHolder.InstructorViewHolder

class InstructorAdapter(val context: Context,val instructor: Instructor, val arrayListStr: ArrayList<String>) : RecyclerView.Adapter<InstructorViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InstructorViewHolder {
        val layoutView = LayoutInflater.from(context).inflate(R.layout.instructor_item, parent, false)
        return InstructorViewHolder(layoutView)
    }

    override fun getItemCount(): Int {
        return arrayListStr.size
    }

    override fun onBindViewHolder(holder: InstructorViewHolder, position: Int) {

    }
}
