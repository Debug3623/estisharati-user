package digital.upbeat.estisharati_user.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import digital.upbeat.estisharati_user.R
import digital.upbeat.estisharati_user.UI.ConsultantDetails
import digital.upbeat.estisharati_user.UI.LegalAdvice
import digital.upbeat.estisharati_user.ViewHolder.OnlineCoursesViewHolder

class LegalAdviceAdapter(val context: Context,val legalAdvice: LegalAdvice, val arrayListStr: ArrayList<String>) : RecyclerView.Adapter<OnlineCoursesViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnlineCoursesViewHolder {
        val layoutView = LayoutInflater.from(context).inflate(R.layout.legal_advice_item, parent, false)
        return OnlineCoursesViewHolder(layoutView)
    }

    override fun getItemCount(): Int {
        return arrayListStr.size
    }

    override fun onBindViewHolder(holder: OnlineCoursesViewHolder, position: Int) {

        holder.parent_layout.setOnClickListener {
            context.startActivity(Intent(context,ConsultantDetails::class.java))
        }
    }
}
