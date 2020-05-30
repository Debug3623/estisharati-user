package digital.upbeat.estisharati_user.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import digital.upbeat.estisharati_user.Fragment.Consultations
import digital.upbeat.estisharati_user.R
import digital.upbeat.estisharati_user.UI.MyConsultations
import digital.upbeat.estisharati_user.ViewHolder.MyConsultationsViewHolder

class MyConsultationsAdapter(val context: Context, val myConsultations: MyConsultations, val arrayListStr: ArrayList<String>) : RecyclerView.Adapter<MyConsultationsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyConsultationsViewHolder {
        val layoutView = LayoutInflater.from(context).inflate(R.layout.my_consultations_item, parent, false)
        return MyConsultationsViewHolder(layoutView)
    }

    override fun getItemCount(): Int {
        return arrayListStr.size
    }

    override fun onBindViewHolder(holder: MyConsultationsViewHolder, position: Int) {
when(position){
    0->{
        holder.consul_name.text="Sufyan Al-Nasr"
        holder.consul_profession.text="Legal Consultation"
        holder.contact_type.text="Video"
        holder.contact_image.setImageResource(R.drawable.ic_video)
    }
    1->{
        holder.consul_name.text="Muhammadi Masoud"
        holder.consul_profession.text="Finance & Accounting"
        holder.contact_type.text="Voice"
        holder.contact_image.setImageResource(R.drawable.ic_voice)
    }
    2->{
        holder.consul_name.text="Bashir Al-Abbas"
        holder.consul_profession.text="Human Development"
        holder.contact_type.text="Chat"
        holder.contact_image.setImageResource(R.drawable.ic_chat)
    }
    3->{
        holder.consul_name.text="Sufyan Al-Nasr"
        holder.consul_profession.text="Legal Consultation"
        holder.contact_type.text="Video"
        holder.contact_image.setImageResource(R.drawable.ic_video)
    }
    4->{
        holder.consul_name.text="Muhammadi Masoud"
        holder.consul_profession.text="Finance & Accounting"
        holder.contact_type.text="Voice"
        holder.contact_image.setImageResource(R.drawable.ic_voice)
    }
    5->{
        holder.consul_name.text="Bashir Al-Abbas"
        holder.consul_profession.text="Human Development"
        holder.contact_type.text="Chat"
        holder.contact_image.setImageResource(R.drawable.ic_chat)
    }
}

    }
}
