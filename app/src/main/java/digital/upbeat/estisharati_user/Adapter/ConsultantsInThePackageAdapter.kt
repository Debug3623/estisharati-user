package digital.upbeat.estisharati_user.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import digital.upbeat.estisharati_user.R
import digital.upbeat.estisharati_user.UI.ConsultantsInThePackage
import digital.upbeat.estisharati_user.ViewHolder.ConsultantsInThePackageViewHolder

class ConsultantsInThePackageAdapter(val context: Context,val consultantsInThePackage: ConsultantsInThePackage, val arrayListStr: ArrayList<String>) : RecyclerView.Adapter<ConsultantsInThePackageViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConsultantsInThePackageViewHolder {
        val layoutView = LayoutInflater.from(context).inflate(R.layout.consultants_in_the_package_item, parent, false)
        return ConsultantsInThePackageViewHolder(layoutView)
    }

    override fun getItemCount(): Int {
        return arrayListStr.size
    }

    override fun onBindViewHolder(holder: ConsultantsInThePackageViewHolder, position: Int) {


    }
}
