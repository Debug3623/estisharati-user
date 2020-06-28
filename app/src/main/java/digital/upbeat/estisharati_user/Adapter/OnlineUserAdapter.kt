package digital.upbeat.estisharati_user.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import digital.upbeat.estisharati_user.DataClassHelper.DataUserFireStore
import digital.upbeat.estisharati_user.Fragment.Home
import digital.upbeat.estisharati_user.Helper.HelperMethods
import digital.upbeat.estisharati_user.R
import digital.upbeat.estisharati_user.UI.ChatHome
import digital.upbeat.estisharati_user.UI.ChatPage
import digital.upbeat.estisharati_user.ViewHolder.OnlineUserViewHolder

class OnlineUserAdapter(val context: Context, val home: Home, val dataUserFireStoreArraylist: ArrayList<DataUserFireStore>) : RecyclerView.Adapter<OnlineUserViewHolder>() {
    lateinit var helperMethods: HelperMethods
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnlineUserViewHolder {
        val layoutView = LayoutInflater.from(context).inflate(R.layout.online_user_item, parent, false)
        return OnlineUserViewHolder(layoutView)
    }

    init {
        helperMethods = HelperMethods(context)
    }

    override fun getItemCount(): Int {
        return dataUserFireStoreArraylist.size
    }

    override fun onBindViewHolder(holder: OnlineUserViewHolder, position: Int) {
        val dataUserFireStore = dataUserFireStoreArraylist.get(position)
        if (position == dataUserFireStoreArraylist.size - 1) {
            holder.user_layout.visibility = View.GONE
            holder.all_btn.visibility = View.VISIBLE
            holder.all_btn.setOnClickListener {
                context.startActivity(Intent(context, ChatHome::class.java))
            }
        } else {
            holder.user_layout.visibility = View.VISIBLE
            holder.all_btn.visibility = View.GONE
            holder.user_layout.setOnClickListener {
                val intent = Intent(context, ChatPage::class.java)
                intent.putExtra("user_id", dataUserFireStore.user_id)
                context.startActivity(intent)
            }
            Glide.with(context).load(dataUserFireStore.image).apply(helperMethods.profileRequestOption).into(holder.profile_picture)
            holder.name.text = dataUserFireStore.fname + " " + dataUserFireStore.lname
            if (dataUserFireStore.user_type.equals("user")) {
                holder.nectie.visibility = View.GONE
            } else if (dataUserFireStore.user_type.equals("consultant")) {
                holder.nectie.visibility = View.VISIBLE
            }
        }
    }
}
