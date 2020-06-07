package digital.upbeat.estisharati_user.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import digital.upbeat.estisharati_user.Fragment.Consultations
import digital.upbeat.estisharati_user.Fragment.CourseContent
import digital.upbeat.estisharati_user.Fragment.Documents
import digital.upbeat.estisharati_user.Fragment.PreviewCourseContent
import digital.upbeat.estisharati_user.R
import digital.upbeat.estisharati_user.UI.MyConsultations
import digital.upbeat.estisharati_user.UI.MyCourses
import digital.upbeat.estisharati_user.UI.OnlineCourses
import digital.upbeat.estisharati_user.ViewHolder.*

class DocumentAdapter(val context: Context, val documents: Documents, val arrayListStr: ArrayList<String>) : RecyclerView.Adapter<DocumentViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocumentViewHolder {
        val layoutView = LayoutInflater.from(context).inflate(R.layout.document_item, parent, false)
        return DocumentViewHolder(layoutView)
    }

    override fun getItemCount(): Int {
        return arrayListStr.size
    }

    override fun onBindViewHolder(holder: DocumentViewHolder, position: Int) {
        holder.prev_cc_sub_icon.setOnClickListener {
            holder.prev_cc_sub_text.setTextColor(ContextCompat.getColorStateList(context, R.color.orange))
            holder.prev_cc_sub_icon.setImageResource(R.drawable.ic_download_arrow_green)
        }
    }
}
