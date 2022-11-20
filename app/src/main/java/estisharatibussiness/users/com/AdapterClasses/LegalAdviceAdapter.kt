package estisharatibussiness.users.com.AdapterClasses

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import estisharatibussiness.users.com.DataClassHelperMehtods.Consultant.Data
import estisharatibussiness.users.com.R
import estisharatibussiness.users.com.UserInterfaces.ActivityConsultantDetails
import estisharatibussiness.users.com.UserInterfaces.ActivityLegalAdvice
import estisharatibussiness.users.com.PublicViewHolder.LegalAdviceViewHolder

class LegalAdviceAdapter(val context: Context, val activityLegalAdvice: ActivityLegalAdvice, var consultantsArrayList: ArrayList<Data>) : RecyclerView.Adapter<LegalAdviceViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LegalAdviceViewHolder {
        val layoutView = LayoutInflater.from(context).inflate(R.layout.legal_advice_item, parent, false)
        return LegalAdviceViewHolder(layoutView)
    }

    override fun getItemCount(): Int {
        return consultantsArrayList.size
    }

    override fun onBindViewHolder(holder: LegalAdviceViewHolder, position: Int) {
        Glide.with(context).load(consultantsArrayList.get(position).user.image_path).apply(activityLegalAdvice.helperMethods.requestOption).into(holder.consultantImage)
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
        holder.consultantPrice.text = context.resources.getString(R.string.usd) + " " + activityLegalAdvice.helperMethods.convetDecimalFormat(price)

        holder.consultantJobTitle.text = consultantsArrayList.get(position).user.job_title
        holder.consultantRate.text = consultantsArrayList.get(position).user.rate
        if (activityLegalAdvice.helperMethods.findConsultantIsOnline(consultantsArrayList.get(position).user.id)) holder.online_status.visibility = View.VISIBLE else holder.online_status.visibility = View.GONE
        holder.parentLayout.setOnClickListener {
            val intent = Intent(context, ActivityConsultantDetails::class.java)
            intent.putExtra("consultant_id", consultantsArrayList.get(position).user.id)
            intent.putExtra("category_id", activityLegalAdvice.category_id)
            intent.putExtra("condition", "0")
            intent.putExtra("appointment_date", "")
            intent.putExtra("appointment_time", "")
            intent.putExtra("video","0")
            intent.putExtra("audio","0")
            intent.putExtra("chat","0")
            context.startActivity(intent)
        }
    }
}
