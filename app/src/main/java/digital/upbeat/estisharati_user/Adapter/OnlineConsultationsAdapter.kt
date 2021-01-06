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
import digital.upbeat.estisharati_user.Helper.GlobalData
import digital.upbeat.estisharati_user.Helper.HelperMethods
import digital.upbeat.estisharati_user.R
import digital.upbeat.estisharati_user.UI.ChatHome
import digital.upbeat.estisharati_user.UI.ChatPage
import digital.upbeat.estisharati_user.ViewHolder.ExpConsultationsViewHolder
import digital.upbeat.estisharati_user.ViewHolder.ExpCoursesViewHolder
import digital.upbeat.estisharati_user.ViewHolder.OnlineConsultationsViewHolder
import digital.upbeat.estisharati_user.ViewHolder.OnlineUserViewHolder

class OnlineConsultationsAdapter(val context: Context, val chatHome: ChatHome, val dataUserFireStoreArraylist: ArrayList<DataUserFireStore>) : RecyclerView.Adapter<OnlineConsultationsViewHolder>() {
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
            val intent = Intent(context, ChatPage::class.java)
            intent.putExtra("user_id", dataUserFireStore.user_id)
            intent.putExtra("forward_type", GlobalData.forwardType)
            intent.putExtra("forward_content", GlobalData.forwardContent)
            context.startActivity(intent)
        }
    }
}
