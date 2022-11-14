package estisharatibussiness.users.com.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import estisharatibussiness.users.com.R
import estisharatibussiness.users.com.UserInterface.ActivityCourseDetails
import estisharatibussiness.users.com.UserInterface.ActivityLegalAdvice
import estisharatibussiness.users.com.UserInterface.ActivitySearch
import estisharatibussiness.users.com.ViewHolder.SearchViewHolder

class SearchAdapter(val context: Context, val activitySearch: ActivitySearch, val dataActivitySearchArrayList: ArrayList<ActivitySearch.DataSearch>) : RecyclerView.Adapter<SearchViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val layoutView = LayoutInflater.from(context).inflate(R.layout.search_item, parent, false)
        return SearchViewHolder(layoutView)
    }

    override fun getItemCount(): Int {
        return dataActivitySearchArrayList.size
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.name.text = dataActivitySearchArrayList.get(position).name
        if (dataActivitySearchArrayList.get(position).type.equals("consultation")) {
            holder.image.setImageResource(R.drawable.ic_my_consultations)
        } else if (dataActivitySearchArrayList.get(position).type.equals("course")) {
            holder.image.setImageResource(R.drawable.ic_courses)
        }

        holder.parentLayout.setOnClickListener {
            if (dataActivitySearchArrayList.get(position).type.equals("consultation")) {
                val intent = Intent(context, ActivityLegalAdvice::class.java)
                intent.putExtra("category_id", dataActivitySearchArrayList.get(position).id)
                intent.putExtra("category_name", dataActivitySearchArrayList.get(position).name)
                context.startActivity(intent)
            } else if (dataActivitySearchArrayList.get(position).type.equals("course")) {
                val intent = Intent(context, ActivityCourseDetails::class.java)
                intent.putExtra("courseId", dataActivitySearchArrayList.get(position).id)
                context.startActivity(intent)
            }
        }
    }
}
