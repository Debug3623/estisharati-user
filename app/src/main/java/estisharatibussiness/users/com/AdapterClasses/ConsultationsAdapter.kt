package estisharatibussiness.users.com.AdapterClasses

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import estisharatibussiness.users.com.DataClassHelperMehtods.Category.Data
import estisharatibussiness.users.com.DataClassHelperMehtods.Category.Subcategory
import estisharatibussiness.users.com.FragmentClasses.Consultations
import estisharatibussiness.users.com.R
import estisharatibussiness.users.com.UserInterfaces.ActivityLegalAdvice
import estisharatibussiness.users.com.PublicViewHolder.ConsultationsViewHolder

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
                    val intent = Intent(context, ActivityLegalAdvice::class.java)
                    intent.putExtra("category_id", categoryArrayList.get(position).id)
                    intent.putExtra("category_name", categoryArrayList.get(position).name)
                    context.startActivity(intent)
                }
            }
        } else if (subCategoryArrayList.size > 0) {
            holder.consul_name.text = subCategoryArrayList.get(position).name
            holder.parentLayout.setOnClickListener {
                val intent = Intent(context, ActivityLegalAdvice::class.java)
                intent.putExtra("category_id", subCategoryArrayList.get(position).id)
                intent.putExtra("category_name", subCategoryArrayList.get(position).name)
                context.startActivity(intent)
            }
        }
    }
}
