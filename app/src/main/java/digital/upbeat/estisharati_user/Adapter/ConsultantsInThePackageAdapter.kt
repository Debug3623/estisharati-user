package digital.upbeat.estisharati_user.Adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import digital.upbeat.estisharati_user.DataClassHelper.Packages.Consultant
import digital.upbeat.estisharati_user.R
import digital.upbeat.estisharati_user.UI.ConsultantCategories
import digital.upbeat.estisharati_user.UI.ConsultantsInThePackage
import digital.upbeat.estisharati_user.ViewHolder.ConsultantsInThePackageViewHolder

class ConsultantsInThePackageAdapter(val context: Context, val consultantsInThePackage: ConsultantsInThePackage, val consultantsArrayList: ArrayList<Consultant>) : RecyclerView.Adapter<ConsultantsInThePackageViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConsultantsInThePackageViewHolder {
        val layoutView = LayoutInflater.from(context).inflate(R.layout.consultants_in_the_package_item, parent, false)
        return ConsultantsInThePackageViewHolder(layoutView)
    }

    override fun getItemCount(): Int {
        return consultantsArrayList.size
    }

    override fun onBindViewHolder(holder: ConsultantsInThePackageViewHolder, position: Int) {
        Glide.with(context).load(consultantsArrayList.get(position).image_path).apply(consultantsInThePackage.helperMethods.requestOption).into(holder.consultantImage)
        holder.consultantName.text = consultantsArrayList.get(position).name
        if (consultantsInThePackage.helperMethods.findConsultantIsOnline(consultantsArrayList.get(position).id)) holder.onlineStatus.visibility = View.VISIBLE else holder.onlineStatus.visibility = View.GONE

        holder.consultantLayout.setOnClickListener {
            val intent = Intent(context, ConsultantCategories::class.java)
            intent.putExtra("consultant_id", consultantsArrayList.get(position).id)
            intent.putExtra("category_id", "")
            context.startActivity(intent)
            (context as Activity).finish()

        }
    }
}
