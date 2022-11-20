package estisharatibussiness.users.com.AdapterClasses

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import estisharatibussiness.users.com.DataClassHelperMehtods.Chat.DataUserFireStore
import estisharatibussiness.users.com.FragmentClasses.Home
import estisharatibussiness.users.com.Helper.GlobalData
import estisharatibussiness.users.com.Helper.HelperMethods
import estisharatibussiness.users.com.R
import estisharatibussiness.users.com.UserInterfaces.ChatHomeActivity
import estisharatibussiness.users.com.UserInterfaces.ChatPageActivity
import estisharatibussiness.users.com.PublicViewHolder.OnlineUserViewHolder

class OnlineUserAdapter(val context: Context, val home: Home, private val dataUserFireStoreArraylist: ArrayList<DataUserFireStore>) : RecyclerView.Adapter<OnlineUserViewHolder>() {
    var helperMethods: HelperMethods = HelperMethods(context)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnlineUserViewHolder {
        val layoutView = LayoutInflater.from(context).inflate(R.layout.online_user_item, parent, false)
        return OnlineUserViewHolder(layoutView)
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
                context.startActivity(Intent(context, ChatHomeActivity::class.java))
            }
        } else {
            holder.user_layout.visibility = View.VISIBLE
            holder.all_btn.visibility = View.GONE
            holder.user_layout.setOnClickListener {
                val intent = Intent(context, ChatPageActivity::class.java)
                intent.putExtra("user_id", dataUserFireStore.user_id)
                intent.putExtra("forward_type", GlobalData.forwardType)
                intent.putExtra("forward_content", GlobalData.forwardContent)
                context.startActivity(intent)
                GlobalData.forwardType = ""
                GlobalData.forwardContent = ""
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
