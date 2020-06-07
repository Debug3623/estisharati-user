package digital.upbeat.estisharati_user.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import digital.upbeat.estisharati_user.Fragment.Consultations
import digital.upbeat.estisharati_user.R
import digital.upbeat.estisharati_user.UI.*
import digital.upbeat.estisharati_user.ViewHolder.*

class RecentChatAdapter(val context: Context, val chatHome: ChatHome, val arrayListStr: ArrayList<String>) : RecyclerView.Adapter<RecentChatViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentChatViewHolder {
        val layoutView = LayoutInflater.from(context).inflate(R.layout.recent_chat_item, parent, false)
        return RecentChatViewHolder(layoutView)
    }

    override fun getItemCount(): Int {
        return arrayListStr.size
    }

    override fun onBindViewHolder(holder: RecentChatViewHolder, position: Int) {
        holder.parent_layout.setOnClickListener { context.startActivity(Intent(context, ChatPage::class.java)) }
    }
}
