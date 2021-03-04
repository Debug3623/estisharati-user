package digital.upbeat.estisharati_user.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import digital.upbeat.estisharati_user.DataClassHelper.Consultant.Data
import digital.upbeat.estisharati_user.R
import digital.upbeat.estisharati_user.UI.ConsultantDetails
import digital.upbeat.estisharati_user.UI.LegalAdvice
import digital.upbeat.estisharati_user.ViewHolder.LegalAdviceViewHolder

class LegalAdviceAdapter(val context: Context, val legalAdvice: LegalAdvice, var consultantsArrayList: ArrayList<Data>) : RecyclerView.Adapter<LegalAdviceViewHolder>() {
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
        var price = 0.0
        if (consultantsArrayList.get(position).user.offer_chat_fee.equals("0")) {
            price += consultantsArrayList.get(position).user.chat_fee.toDouble()
        } else {
            price += consultantsArrayList.get(position).user.offer_chat_fee.toDouble()
        }
        if (consultantsArrayList.get(position).user.offer_voice_fee.equals("0")) {
            price += consultantsArrayList.get(position).user.voice_fee.toDouble()
        } else {
            price += consultantsArrayList.get(position).user.offer_voice_fee.toDouble()
        }
        if (consultantsArrayList.get(position).user.offer_video_fee.equals("0")) {
            price += consultantsArrayList.get(position).user.video_fee.toDouble()
        } else {
            price += consultantsArrayList.get(position).user.offer_video_fee.toDouble()
        }
        holder.consultantPrice.text = context.resources.getString(R.string.aed) + " " + legalAdvice.helperMethods.convetDecimalFormat(price)

        holder.consultantJobTitle.text = consultantsArrayList.get(position).user.job_title
        holder.consultantRate.text = consultantsArrayList.get(position).user.rate
        if (legalAdvice.helperMethods.findConsultantIsOnline(consultantsArrayList.get(position).user.id)) holder.online_status.visibility = View.VISIBLE else holder.online_status.visibility = View.GONE
        holder.parentLayout.setOnClickListener {
            val intent = Intent(context, ConsultantDetails::class.java)
            intent.putExtra("consultant_id", consultantsArrayList.get(position).user.id)
            intent.putExtra("category_id", legalAdvice.category_id)
            context.startActivity(intent)
        }
    }
}
