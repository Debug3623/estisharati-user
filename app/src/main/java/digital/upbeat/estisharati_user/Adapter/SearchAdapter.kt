package digital.upbeat.estisharati_user.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import digital.upbeat.estisharati_user.R
import digital.upbeat.estisharati_user.UI.CourseDetails
import digital.upbeat.estisharati_user.UI.LegalAdvice
import digital.upbeat.estisharati_user.UI.Search
import digital.upbeat.estisharati_user.ViewHolder.SearchViewHolder

class SearchAdapter(val context: Context, val search: Search, val dataSearchArrayList: ArrayList<Search.DataSearch>) : RecyclerView.Adapter<SearchViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val layoutView = LayoutInflater.from(context).inflate(R.layout.search_item, parent, false)
        return SearchViewHolder(layoutView)
    }

    override fun getItemCount(): Int {
        return dataSearchArrayList.size
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.name.text = dataSearchArrayList.get(position).name
        if (dataSearchArrayList.get(position).type.equals("consultation")) {
            holder.image.setImageResource(R.drawable.ic_my_consultations)
        } else if (dataSearchArrayList.get(position).type.equals("course")) {
            holder.image.setImageResource(R.drawable.ic_courses)
        }

        holder.parentLayout.setOnClickListener {
            if (dataSearchArrayList.get(position).type.equals("consultation")) {
                val intent = Intent(context, LegalAdvice::class.java)
                intent.putExtra("category_id", dataSearchArrayList.get(position).id)
                intent.putExtra("category_name", dataSearchArrayList.get(position).name)
                context.startActivity(intent)
            } else if (dataSearchArrayList.get(position).type.equals("course")) {
                val intent = Intent(context, CourseDetails::class.java)
                intent.putExtra("courseId", dataSearchArrayList.get(position).id)
                context.startActivity(intent)
            }
        }
    }
}
