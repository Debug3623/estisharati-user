package digital.upbeat.estisharati_user.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import digital.upbeat.estisharati_user.Fragment.Home
import digital.upbeat.estisharati_user.R
import digital.upbeat.estisharati_user.UI.ChatHome
import digital.upbeat.estisharati_user.ViewHolder.ExpConsultationsViewHolder
import digital.upbeat.estisharati_user.ViewHolder.ExpCoursesViewHolder
import digital.upbeat.estisharati_user.ViewHolder.OnlineUserViewHolder

class OnlineUserAdapter(val context: Context, val home: Home, val arrayListStr: ArrayList<String>) : RecyclerView.Adapter<OnlineUserViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnlineUserViewHolder {
        val layoutView = LayoutInflater.from(context).inflate(R.layout.online_user_item, parent, false)
        return OnlineUserViewHolder(layoutView)
    }

    override fun getItemCount(): Int {
        return arrayListStr.size
    }

    override fun onBindViewHolder(holder: OnlineUserViewHolder, position: Int) {
        if (position == arrayListStr.size - 1) {
            holder.user_layout.visibility = View.GONE
            holder.all_btn.visibility = View.VISIBLE
            holder.all_btn.setOnClickListener {
                context.startActivity(Intent(context, ChatHome::class.java))
            }
        } else {
            holder.user_layout.visibility = View.VISIBLE
            holder.all_btn.visibility = View.GONE
        }
    }
}
