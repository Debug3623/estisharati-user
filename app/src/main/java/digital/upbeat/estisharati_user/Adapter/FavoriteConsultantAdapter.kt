package digital.upbeat.estisharati_user.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import digital.upbeat.estisharati_user.DataClassHelper.Favourites.Consultant
import digital.upbeat.estisharati_user.R
import digital.upbeat.estisharati_user.UI.ConsultantDetails
import digital.upbeat.estisharati_user.UI.Favorites
import digital.upbeat.estisharati_user.ViewHolder.FavoriteConsultantViewHolder

class FavoriteConsultantAdapter(val context: Context, val favorites: Favorites, val consultantsArrayList: ArrayList<Consultant>) : RecyclerView.Adapter<FavoriteConsultantViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteConsultantViewHolder {
        val layoutView = LayoutInflater.from(context).inflate(R.layout.favorite_consultant_item, parent, false)
        return FavoriteConsultantViewHolder(layoutView)
    }

    override fun getItemCount(): Int {
        return consultantsArrayList.size
    }

    override fun onBindViewHolder(holder: FavoriteConsultantViewHolder, position: Int) {
        Glide.with(context).load(consultantsArrayList.get(position).consultant.image_path).apply(favorites.helperMethods.profileRequestOption).into(holder.consultantImage)
        holder.consultantName.text = consultantsArrayList.get(position).consultant.name
        holder.consultantJobTitle.text = consultantsArrayList.get(position).consultant.job_title
        holder.consultantRate.text = consultantsArrayList.get(position).consultant.rate

        var price = 0.0
        if (consultantsArrayList.get(position).consultant.offer_chat_fee.equals("0")) {
            price += consultantsArrayList.get(position).consultant.chat_fee.toDouble()
        } else {
            price += consultantsArrayList.get(position).consultant.offer_chat_fee.toDouble()
        }
        if (consultantsArrayList.get(position).consultant.offer_voice_fee.equals("0")) {
            price += consultantsArrayList.get(position).consultant.voice_fee.toDouble()
        } else {
            price += consultantsArrayList.get(position).consultant.offer_voice_fee.toDouble()
        }
        if (consultantsArrayList.get(position).consultant.offer_video_fee.equals("0")) {
            price += consultantsArrayList.get(position).consultant.video_fee.toDouble()
        } else {
            price += consultantsArrayList.get(position).consultant.offer_video_fee.toDouble()
        }
        holder.consultantPrice.text = context.resources.getString(R.string.usd) + " " + favorites.helperMethods.convetDecimalFormat(price)

        if (favorites.helperMethods.findConsultantIsOnline(consultantsArrayList.get(position).consultant_id)) holder.onlineStatus.visibility = View.VISIBLE else holder.onlineStatus.visibility = View.GONE

        holder.parentLayout.setOnClickListener {
            val intent = Intent(context, ConsultantDetails::class.java)
            intent.putExtra("consultant_id", consultantsArrayList.get(position).consultant.id)
            intent.putExtra("category_id", "")
            context.startActivity(intent)
        }
    }
}
