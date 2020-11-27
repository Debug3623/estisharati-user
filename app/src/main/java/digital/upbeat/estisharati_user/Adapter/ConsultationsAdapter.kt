package digital.upbeat.estisharati_user.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import digital.upbeat.estisharati_user.DataClassHelper.Home.Category
import digital.upbeat.estisharati_user.Fragment.Consultations
import digital.upbeat.estisharati_user.Fragment.Home
import digital.upbeat.estisharati_user.R
import digital.upbeat.estisharati_user.UI.LegalAdvice
import digital.upbeat.estisharati_user.ViewHolder.ConsultationsViewHolder
import digital.upbeat.estisharati_user.ViewHolder.ExpConsultationsViewHolder

class ConsultationsAdapter(val context: Context, val consultations: Consultations, val categoriesArrayList: ArrayList<Category>) : RecyclerView.Adapter<ConsultationsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConsultationsViewHolder {
        val layoutView = LayoutInflater.from(context).inflate(R.layout.consultations_item, parent, false)
        return ConsultationsViewHolder(layoutView)
    }

    override fun getItemCount(): Int {
        return categoriesArrayList.size
    }

    override fun onBindViewHolder(holder: ConsultationsViewHolder, position: Int) {
        holder.consul_name.text = categoriesArrayList.get(position).name
        holder.parentLayout.setOnClickListener {
            val intent = Intent(context, LegalAdvice::class.java)
            intent.putExtra("category_id", categoriesArrayList.get(position).id)
            intent.putExtra("category_name", categoriesArrayList.get(position).name)
            context.startActivity(intent)
        }
    }
}
