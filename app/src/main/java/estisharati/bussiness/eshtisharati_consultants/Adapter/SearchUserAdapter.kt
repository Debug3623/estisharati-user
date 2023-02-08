package estisharati.bussiness.eshtisharati_consultants.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import estisharati.bussiness.eshtisharati_consultants.DataClassHelper.RecentChat.DataUserFireStore
import estisharati.bussiness.eshtisharati_consultants.Helper.GlobalData
import estisharati.bussiness.eshtisharati_consultants.Helper.HelperMethods
import estisharati.bussiness.eshtisharati_consultants.R
import estisharati.bussiness.eshtisharati_consultants.UI.ChatPage
import estisharati.bussiness.eshtisharati_consultants.UI.ConsultantDrawer
import estisharati.bussiness.eshtisharati_consultants.ViewHolder.SearchUserViewHolder

class SearchUserAdapter(val context: Context, val consultantDrawer: ConsultantDrawer, val datauserfirestoreArrayList: ArrayList<DataUserFireStore>) : RecyclerView.Adapter<SearchUserViewHolder>() {
    val helperMethods: HelperMethods

    init {
        helperMethods = HelperMethods(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchUserViewHolder {
        val layoutView = LayoutInflater.from(context).inflate(R.layout.search_user_item, parent, false)
        return SearchUserViewHolder(layoutView)
    }

    override fun getItemCount(): Int {
        return datauserfirestoreArrayList.size
    }

    override fun onBindViewHolder(holder: SearchUserViewHolder, position: Int) {
        val dataUserFireStore = datauserfirestoreArrayList.get(position)
        Glide.with(context).load(dataUserFireStore.image).apply(helperMethods.profileRequestOption).into(holder.profile_picture)
        holder.user_name.text = "${dataUserFireStore.fname} ${dataUserFireStore.lname}"

        if (dataUserFireStore.online_status) {
            holder.last_seen_time.visibility = View.GONE
            holder.online_status.visibility = View.VISIBLE
        } else {
            holder.last_seen_time.visibility = View.VISIBLE
            holder.online_status.visibility = View.GONE
            holder.last_seen_time.text = helperMethods.getFormattedDateShort(dataUserFireStore.last_seen)
        }
        if (dataUserFireStore.user_type.equals("user")) {
            holder.nectie.visibility = View.GONE
        } else if (dataUserFireStore.user_type.equals("consultant")) {
            holder.nectie.visibility = View.VISIBLE
        }
        holder.parent_layout.setOnClickListener {
            consultantDrawer.searchUserdialog?.dismiss()
            val intent = Intent(context, ChatPage::class.java)
            intent.putExtra("user_id", dataUserFireStore.user_id)
            intent.putExtra("forward_type", GlobalData.forwardType)
            intent.putExtra("forward_content", GlobalData.forwardContent)
            context.startActivity(intent)
            GlobalData.forwardType = ""
            GlobalData.forwardContent = ""
        }
    }
}
