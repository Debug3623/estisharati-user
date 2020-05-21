package digital.upbeat.estisharati_user.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import digital.upbeat.estisharati_user.Fragment.Consultations
import digital.upbeat.estisharati_user.Fragment.Home
import digital.upbeat.estisharati_user.R
import digital.upbeat.estisharati_user.ViewHolder.ConsultationsViewHolder
import digital.upbeat.estisharati_user.ViewHolder.ExpConsultationsViewHolder

class ConsultationsAdapter(val context: Context, val consultations: Consultations,val arrayListStr: ArrayList<String>) : RecyclerView.Adapter<ConsultationsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConsultationsViewHolder {
        val layoutView = LayoutInflater.from(context).inflate(R.layout.consultations_item, parent, false)
        return ConsultationsViewHolder(layoutView)
    }

    override fun getItemCount(): Int {
        return arrayListStr.size
    }

    override fun onBindViewHolder(holder: ConsultationsViewHolder, position: Int) {
        holder.consul_name.text=arrayListStr.get(position)
    }
}
