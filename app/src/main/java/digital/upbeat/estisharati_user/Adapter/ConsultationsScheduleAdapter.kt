package digital.upbeat.estisharati_user.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import digital.upbeat.estisharati_user.DataClassHelper.ConsultantDetails.Categories
import digital.upbeat.estisharati_user.DataClassHelper.Home.Category
import digital.upbeat.estisharati_user.Fragment.Consultations
import digital.upbeat.estisharati_user.Fragment.Home
import digital.upbeat.estisharati_user.R
import digital.upbeat.estisharati_user.UI.ConsultantCategories
import digital.upbeat.estisharati_user.UI.ConsultantDetails
import digital.upbeat.estisharati_user.UI.LegalAdvice
import digital.upbeat.estisharati_user.ViewHolder.ConsultationsViewHolder
import digital.upbeat.estisharati_user.ViewHolder.ExpConsultationsViewHolder

class ConsultationsScheduleAdapter(val context: Context, val consultantDetails: ConsultantCategories, val categoriesArrayList: ArrayList<Categories>) : RecyclerView.Adapter<ConsultationsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConsultationsViewHolder {
        val layoutView = LayoutInflater.from(context).inflate(R.layout.consultations_item, parent, false)
        return ConsultationsViewHolder(layoutView)
    }

    override fun getItemCount(): Int {
        return categoriesArrayList.size
    }

    override fun onBindViewHolder(holder: ConsultationsViewHolder, position: Int) {
        holder.consul_name.text = categoriesArrayList.get(position).category.name
        holder.parentLayout.setOnClickListener {
            consultantDetails.categoryId = (categoriesArrayList.get(position).category.category_id)
            consultantDetails.showConsultationPriceDetailsPopup()
        }
    }
}
