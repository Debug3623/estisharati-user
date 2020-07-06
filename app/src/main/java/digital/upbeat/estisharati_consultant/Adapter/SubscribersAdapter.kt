package digital.upbeat.estisharati_consultant.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import digital.upbeat.estisharati_consultant.Fragment.Subscribers
import digital.upbeat.estisharati_consultant.R
import digital.upbeat.estisharati_consultant.UI.ChatPage
import digital.upbeat.estisharati_consultant.ViewHolder.SubscribersViewHolder

class SubscribersAdapter(val context: Context, val subscribers: Subscribers, val arrayListStr: ArrayList<String>) : RecyclerView.Adapter<SubscribersViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubscribersViewHolder {
        val layoutView = LayoutInflater.from(context).inflate(R.layout.subscribers_recent_chat_item, parent, false)
        return SubscribersViewHolder(layoutView)
    }

    override fun getItemCount(): Int {
        return arrayListStr.size
    }

    override fun onBindViewHolder(holder: SubscribersViewHolder, position: Int) {
        holder.parent_layout.setOnClickListener {
            context.startActivity(Intent(context,ChatPage::class.java))
        }
    }
}
