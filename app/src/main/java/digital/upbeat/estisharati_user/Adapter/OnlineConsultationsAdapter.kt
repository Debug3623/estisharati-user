package digital.upbeat.estisharati_user.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import digital.upbeat.estisharati_user.Fragment.Home
import digital.upbeat.estisharati_user.R
import digital.upbeat.estisharati_user.UI.ChatHome
import digital.upbeat.estisharati_user.ViewHolder.ExpConsultationsViewHolder
import digital.upbeat.estisharati_user.ViewHolder.ExpCoursesViewHolder
import digital.upbeat.estisharati_user.ViewHolder.OnlineConsultationsViewHolder
import digital.upbeat.estisharati_user.ViewHolder.OnlineUserViewHolder

class OnlineConsultationsAdapter(val context: Context, val chatHome: ChatHome, val arrayListStr: ArrayList<String>) : RecyclerView.Adapter<OnlineConsultationsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnlineConsultationsViewHolder {
        val layoutView = LayoutInflater.from(context).inflate(R.layout.online_consultations_item, parent, false)
        return OnlineConsultationsViewHolder(layoutView)
    }

    override fun getItemCount(): Int {
        return arrayListStr.size
    }

    override fun onBindViewHolder(holder: OnlineConsultationsViewHolder, position: Int) {

    }
}
