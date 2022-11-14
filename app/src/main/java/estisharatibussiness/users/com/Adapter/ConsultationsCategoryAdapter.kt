package estisharatibussiness.users.com.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import estisharatibussiness.users.com.DataClassHelper.ConsultantDetails.Categories
import estisharatibussiness.users.com.R
import estisharatibussiness.users.com.UserInterface.ActivityConsultantDetails
import estisharatibussiness.users.com.ViewHolder.ConsultationsViewHolder

class ConsultationsCategoryAdapter(val context: Context, val activityConsultantDetails: ActivityConsultantDetails, val categoriesArrayList: ArrayList<Categories>) : RecyclerView.Adapter<ConsultationsViewHolder>() {
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
            activityConsultantDetails.categoryId = (categoriesArrayList.get(position).category.category_id)
            activityConsultantDetails.showConsultationPriceDetailsPopup()
        }
    }
}
