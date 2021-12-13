package digital.upbeat.estisharati_user.Adapter

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import digital.upbeat.estisharati_user.DataClassHelper.Offers.Consultant
import digital.upbeat.estisharati_user.R
import digital.upbeat.estisharati_user.UI.ConsultantDetails
import digital.upbeat.estisharati_user.UI.Offers
import digital.upbeat.estisharati_user.ViewHolder.OffersConsultantsViewHolder

class OffersConsultantsAdapter(val context: Context, val offers: Offers, val consultantsArrayList: ArrayList<Consultant>) : RecyclerView.Adapter<OffersConsultantsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OffersConsultantsViewHolder {
        val layoutView = LayoutInflater.from(context).inflate(R.layout.offers_consultants_item, parent, false)
        return OffersConsultantsViewHolder(layoutView)
    }

    override fun getItemCount(): Int {
        return consultantsArrayList.size
    }

    override fun onBindViewHolder(holder: OffersConsultantsViewHolder, position: Int) {
        Glide.with(context).load(consultantsArrayList.get(position).consultant.image_path).apply(offers.helperMethods.profileRequestOption).into(holder.offersConsultantImage)
        holder.offersConsultantName.text = consultantsArrayList.get(position).consultant.name
        holder.offersConsultantRate.text = consultantsArrayList.get(position).consultant.rate
        holder.offersConsultantJobTitle.text = consultantsArrayList.get(position).consultant.job_title
        holder.offersConsultantEndDate.text = consultantsArrayList.get(position).enddate


        holder.offersNewConsultantPrice.text = context.getString(R.string.usd) + " " + consultantsArrayList.get(position).offerprice
        holder.offersOldConsultantPrice.text = consultantsArrayList.get(position).consultant.price
        holder.offersOldConsultantPrice.setPaintFlags(holder.offersOldConsultantPrice.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)
        if (offers.helperMethods.findConsultantIsOnline(consultantsArrayList.get(position).consultant_id)) holder.onlineStatus.visibility = View.VISIBLE else holder.onlineStatus.visibility = View.GONE

        holder.offersConsultantParentLayout.setOnClickListener {
            val intent = Intent(context, ConsultantDetails::class.java)
            intent.putExtra("consultant_id", consultantsArrayList.get(position).consultant.id)
            intent.putExtra("category_id", "")
            context.startActivity(intent)
        }
    }
}
