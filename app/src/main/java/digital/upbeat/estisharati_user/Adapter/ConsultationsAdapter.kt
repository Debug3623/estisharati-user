package digital.upbeat.estisharati_user.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import digital.upbeat.estisharati_user.DataClassHelper.Category.Data
import digital.upbeat.estisharati_user.DataClassHelper.Category.Subcategory
import digital.upbeat.estisharati_user.DataClassHelper.Home.Category
import digital.upbeat.estisharati_user.Fragment.Consultations
import digital.upbeat.estisharati_user.Fragment.Home
import digital.upbeat.estisharati_user.R
import digital.upbeat.estisharati_user.UI.LegalAdvice
import digital.upbeat.estisharati_user.ViewHolder.ConsultationsViewHolder
import digital.upbeat.estisharati_user.ViewHolder.ExpConsultationsViewHolder

class ConsultationsAdapter(val context: Context, val consultations: Consultations, val categoryArrayList: ArrayList<Data>, val subCategoryArrayList: ArrayList<Subcategory>) : RecyclerView.Adapter<ConsultationsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConsultationsViewHolder {
        val layoutView = LayoutInflater.from(context).inflate(R.layout.consultations_item, parent, false)
        return ConsultationsViewHolder(layoutView)
    }

    override fun getItemCount(): Int {
        return if (categoryArrayList.size > 0) {
            categoryArrayList.size
        } else if (subCategoryArrayList.size > 0) {
            subCategoryArrayList.size
        } else {
            0
        }
    }

    override fun onBindViewHolder(holder: ConsultationsViewHolder, position: Int) {
        if (categoryArrayList.size > 0) {
            holder.consul_name.text = categoryArrayList.get(position).name
            holder.parentLayout.setOnClickListener {
                if (categoryArrayList.get(position).subcategories.size > 0) {
                    consultations.InitializeRecyclerview(arrayListOf(), categoryArrayList.get(position).subcategories)
                } else {
                    val intent = Intent(context, LegalAdvice::class.java)
                    intent.putExtra("category_id", categoryArrayList.get(position).id)
                    intent.putExtra("category_name", categoryArrayList.get(position).name)
                    context.startActivity(intent)
                }
            }
        } else if (subCategoryArrayList.size > 0) {
            holder.consul_name.text = subCategoryArrayList.get(position).name
            holder.parentLayout.setOnClickListener {
                val intent = Intent(context, LegalAdvice::class.java)
                intent.putExtra("category_id", subCategoryArrayList.get(position).id)
                intent.putExtra("category_name", subCategoryArrayList.get(position).name)
                context.startActivity(intent)
            }
        }
    }
}
