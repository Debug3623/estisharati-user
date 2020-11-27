package digital.upbeat.estisharati_user.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import digital.upbeat.estisharati_user.DataClassHelper.Consultant.Consultant
import digital.upbeat.estisharati_user.R
import digital.upbeat.estisharati_user.UI.ConsultantDetails
import digital.upbeat.estisharati_user.UI.LegalAdvice
import digital.upbeat.estisharati_user.ViewHolder.LegalAdviceViewHolder

class LegalAdviceAdapter(val context: Context, val legalAdvice: LegalAdvice, var consultantsArrayList: ArrayList<Consultant>) : RecyclerView.Adapter<LegalAdviceViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LegalAdviceViewHolder {
        val layoutView = LayoutInflater.from(context).inflate(R.layout.legal_advice_item, parent, false)
        return LegalAdviceViewHolder(layoutView)
    }

    override fun getItemCount(): Int {
        return consultantsArrayList.size
    }

    override fun onBindViewHolder(holder: LegalAdviceViewHolder, position: Int) {
        Glide.with(context).load(consultantsArrayList.get(position).user.image_path).apply(legalAdvice.helperMethods.requestOption).into(holder.consultantImage)
        holder.consultantName.text = consultantsArrayList.get(position).user.name
        holder.consultantPrice.text = context.resources.getString(R.string.aed) + "" + consultantsArrayList.get(position).user.price
        holder.consultantJobTitle.text = consultantsArrayList.get(position).user.job_title
        holder.consultantRate.text = consultantsArrayList.get(position).user.rate


        holder.parentLayout.setOnClickListener {
          val intent=  Intent(context, ConsultantDetails::class.java)
            intent.putExtra("consultant_id",consultantsArrayList.get(position).user.id)
            context.startActivity(intent)
        }
    }
}
