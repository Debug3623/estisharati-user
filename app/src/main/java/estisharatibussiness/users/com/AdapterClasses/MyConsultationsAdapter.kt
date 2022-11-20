package estisharatibussiness.users.com.AdapterClasses

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import estisharatibussiness.users.com.DataClassHelperMehtods.MyConsultation.Data
import estisharatibussiness.users.com.R
import estisharatibussiness.users.com.UserInterfaces.ActivityConsultationDetailsVideo
import estisharatibussiness.users.com.UserInterfaces.ActivityMyConsultations
import estisharatibussiness.users.com.PublicViewHolder.MyConsultationsViewHolder

class MyConsultationsAdapter(val context: Context, val activityMyConsultations: ActivityMyConsultations, val myConsultationArrayList: ArrayList<Data>) : RecyclerView.Adapter<MyConsultationsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyConsultationsViewHolder {
        val layoutView = LayoutInflater.from(context).inflate(R.layout.my_consultations_item, parent, false)
        return MyConsultationsViewHolder(layoutView)
    }

    override fun getItemCount(): Int {
        return myConsultationArrayList.size
    }

    override fun onBindViewHolder(holder: MyConsultationsViewHolder, position: Int) {

        if (activityMyConsultations.helperMethods.findConsultantIsOnline(myConsultationArrayList.get(position).consultant_id)) holder.online_status.visibility = View.VISIBLE else holder.online_status.visibility = View.GONE

        holder.consultantName.text = myConsultationArrayList.get(position).name
        holder.consultantCategory.text = myConsultationArrayList.get(position).category_name
        Glide.with(context).load(myConsultationArrayList.get(position).image_path).apply(activityMyConsultations.helperMethods.requestOption).into(holder.consultantImage)
        if (myConsultationArrayList.get(position).chat) {
            holder.chatOption.visibility = View.VISIBLE
        } else {
            holder.chatOption.visibility = View.GONE
        }
        if (myConsultationArrayList.get(position).audio) {
            holder.voiceOption.visibility = View.VISIBLE
        } else {
            holder.voiceOption.visibility = View.GONE
        }
        if (myConsultationArrayList.get(position).video) {
            holder.videoOption.visibility = View.VISIBLE
        } else {
            holder.videoOption.visibility = View.GONE
        }
        holder.myConsultationsLayout.setOnClickListener {
            val intent = Intent(context, ActivityConsultationDetailsVideo::class.java)
            intent.putParcelableArrayListExtra("myConsultationArrayList", myConsultationArrayList)
            intent.putExtra("position",position)
            context.startActivity(intent)
        }
        holder.rateNow.setOnClickListener {
            activityMyConsultations.showRatingPopup(myConsultationArrayList.get(position))
        }
    }
}
