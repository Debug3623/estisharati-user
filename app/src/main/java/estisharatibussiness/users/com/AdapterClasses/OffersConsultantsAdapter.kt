package estisharatibussiness.users.com.AdapterClasses

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import estisharatibussiness.users.com.DataClassHelperMehtods.Offers.Consultant
import estisharatibussiness.users.com.R
import estisharatibussiness.users.com.UserInterfaces.ActivityConsultantDetails
import estisharatibussiness.users.com.UserInterfaces.ActivityOffers
import estisharatibussiness.users.com.PublicViewHolder.OffersConsultantsViewHolder

class OffersConsultantsAdapter(val context: Context, val activityOffers: ActivityOffers, val consultantsArrayList: ArrayList<Consultant>) : RecyclerView.Adapter<OffersConsultantsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OffersConsultantsViewHolder {
        val layoutView = LayoutInflater.from(context).inflate(R.layout.offers_consultants_item, parent, false)
        return OffersConsultantsViewHolder(layoutView)
    }

    override fun getItemCount(): Int {
        return consultantsArrayList.size
    }

    override fun onBindViewHolder(holder: OffersConsultantsViewHolder, position: Int) {
        Glide.with(context).load(consultantsArrayList.get(position).consultant.image_path).apply(activityOffers.helperMethods.profileRequestOption).into(holder.offersConsultantImage)
        holder.offersConsultantName.text = consultantsArrayList.get(position).consultant.name
        holder.offersConsultantRate.text = consultantsArrayList.get(position).consultant.rate
        holder.offersConsultantJobTitle.text = consultantsArrayList.get(position).consultant.job_title
        holder.offersConsultantEndDate.text = consultantsArrayList.get(position).enddate


        holder.offersNewConsultantPrice.text = context.getString(R.string.usd) + " " + consultantsArrayList.get(position).offerprice
        holder.offersOldConsultantPrice.text = consultantsArrayList.get(position).consultant.price
        holder.offersOldConsultantPrice.paintFlags = holder.offersOldConsultantPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        if (activityOffers.helperMethods.findConsultantIsOnline(consultantsArrayList.get(position).consultant_id)) holder.onlineStatus.visibility = View.VISIBLE else holder.onlineStatus.visibility = View.GONE

        holder.offersConsultantParentLayout.setOnClickListener {
            val intent = Intent(context, ActivityConsultantDetails::class.java)
            intent.putExtra("consultant_id", consultantsArrayList.get(position).consultant.id)
            intent.putExtra("category_id", "")
            context.startActivity(intent)
        }
    }
}
