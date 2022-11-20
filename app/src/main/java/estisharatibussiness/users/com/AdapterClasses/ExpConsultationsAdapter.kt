package estisharatibussiness.users.com.AdapterClasses

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import estisharatibussiness.users.com.DataClassHelperMehtods.Home.Category
import estisharatibussiness.users.com.FragmentClasses.Home
import estisharatibussiness.users.com.R
import estisharatibussiness.users.com.UserInterfaces.ActivityLegalAdvice
import estisharatibussiness.users.com.PublicViewHolder.ExpConsultationsViewHolder

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

            val intent= Intent(context, ActivityLegalAdvice::class.java)
            intent.putExtra("category_id",categoriesArrayList.get(position).id)
            intent.putExtra("category_name",categoriesArrayList.get(position).name)
            context.startActivity(intent)
        }
    }
}
