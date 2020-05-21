package digital.upbeat.estisharati_user.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import digital.upbeat.estisharati_user.Fragment.Home
import digital.upbeat.estisharati_user.R
import digital.upbeat.estisharati_user.ViewHolder.ExpConsultationsViewHolder

class ExpConsultationsAdapter(val context: Context, val home: Home,val arrayListStr: ArrayList<String>) : RecyclerView.Adapter<ExpConsultationsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpConsultationsViewHolder {
        val layoutView = LayoutInflater.from(context).inflate(R.layout.exp_consultations_item, parent, false)
        return ExpConsultationsViewHolder(layoutView)
    }

    override fun getItemCount(): Int {
        return arrayListStr.size
    }

    override fun onBindViewHolder(holder: ExpConsultationsViewHolder, position: Int) {
        holder.exp_consultations_name.text=arrayListStr.get(position)
    }
}
