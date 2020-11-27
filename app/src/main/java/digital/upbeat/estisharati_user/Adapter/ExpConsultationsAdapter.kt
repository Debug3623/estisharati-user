package digital.upbeat.estisharati_user.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import digital.upbeat.estisharati_user.DataClassHelper.Home.Category
import digital.upbeat.estisharati_user.Fragment.Home
import digital.upbeat.estisharati_user.R
import digital.upbeat.estisharati_user.UI.LegalAdvice
import digital.upbeat.estisharati_user.ViewHolder.ExpConsultationsViewHolder

class ExpConsultationsAdapter(val context: Context, val home: Home, val categoriesArrayList: ArrayList<Category>) : RecyclerView.Adapter<ExpConsultationsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpConsultationsViewHolder {
        val layoutView = LayoutInflater.from(context).inflate(R.layout.exp_consultations_item, parent, false)
        return ExpConsultationsViewHolder(layoutView)
    }

    override fun getItemCount(): Int {
        return categoriesArrayList.size
    }

    override fun onBindViewHolder(holder: ExpConsultationsViewHolder, position: Int) {
        holder.exp_consultations_name.text = categoriesArrayList.get(position).name
        holder.exp_consultations_name.setOnClickListener {

            val intent= Intent(context, LegalAdvice::class.java)
            intent.putExtra("category_id",categoriesArrayList.get(position).id)
            intent.putExtra("category_name",categoriesArrayList.get(position).name)
            context.startActivity(intent)
        }
    }
}
