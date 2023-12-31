package estisharatibussiness.users.com.AdapterClasses

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import estisharatibussiness.users.com.DataClassHelperMehtods.Chat.DataUserFireStore
import estisharatibussiness.users.com.Helper.GlobalData
import estisharatibussiness.users.com.Helper.HelperMethods
import estisharatibussiness.users.com.R
import estisharatibussiness.users.com.UserInterfaces.ChatHomeActivity
import estisharatibussiness.users.com.UserInterfaces.ChatPageActivity
import estisharatibussiness.users.com.PublicViewHolder.OnlineConsultationsViewHolder

class OnlineConsultationsAdapter(val context: Context, val chatHomeActivity: ChatHomeActivity, val dataUserFireStoreArraylist: ArrayList<DataUserFireStore>) : RecyclerView.Adapter<OnlineConsultationsViewHolder>() {
    var helperMethods: HelperMethods

    init {
        helperMethods = HelperMethods(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnlineConsultationsViewHolder {
        val layoutView = LayoutInflater.from(context).inflate(R.layout.online_consultations_item, parent, false)
        return OnlineConsultationsViewHolder(layoutView)
    }

    override fun getItemCount(): Int {
        return dataUserFireStoreArraylist.size
    }

    override fun onBindViewHolder(holder: OnlineConsultationsViewHolder, position: Int) {
        val dataUserFireStore = dataUserFireStoreArraylist.get(position)
        Glide.with(context).load(dataUserFireStore.image).apply(helperMethods.profileRequestOption).into(holder.profile_picture)
        holder.name.text = dataUserFireStore.fname + " " + dataUserFireStore.lname
        if (dataUserFireStore.online_status) holder.online_status.visibility = View.VISIBLE else holder.online_status.visibility = View.GONE
        if (dataUserFireStore.user_type.equals("user")) {
            holder.nectie.visibility = View.GONE
        } else if (dataUserFireStore.user_type.equals("consultant")) {
            holder.nectie.visibility = View.VISIBLE
        }
        holder.consultants_layout.setOnClickListener {
            val intent = Intent(context, ChatPageActivity::class.java)
            intent.putExtra("user_id", dataUserFireStore.user_id)
            intent.putExtra("forward_type", GlobalData.forwardType)
            intent.putExtra("forward_content", GlobalData.forwardContent)
            context.startActivity(intent)
            GlobalData.forwardType = ""
            GlobalData.forwardContent = ""
        }
    }
}
