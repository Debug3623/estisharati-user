package digital.upbeat.estisharati_user.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import digital.upbeat.estisharati_user.Fragment.Consultations
import digital.upbeat.estisharati_user.Fragment.CourseContent
import digital.upbeat.estisharati_user.R
import digital.upbeat.estisharati_user.UI.*
import digital.upbeat.estisharati_user.ViewHolder.*

class ChatAdapter(val context: Context, val chatPage: ChatPage, val arrayListStr: ArrayList<String>) : RecyclerView.Adapter<ChatViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val layoutView = LayoutInflater.from(context).inflate(R.layout.chat_item, parent, false)
        return ChatViewHolder(layoutView)
    }

    override fun getItemCount(): Int {
        return arrayListStr.size
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.me_image_layout.setOnClickListener {
            context.startActivity(Intent(context, PhotoViewer::class.java))
        }
        holder.other_image_layout.setOnClickListener {
            context.startActivity(Intent(context, PhotoViewer::class.java))
        }
    }
}
