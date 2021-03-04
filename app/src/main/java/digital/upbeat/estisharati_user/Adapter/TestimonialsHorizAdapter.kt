package digital.upbeat.estisharati_user.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import digital.upbeat.estisharati_user.Fragment.Home
import digital.upbeat.estisharati_user.R
import digital.upbeat.estisharati_user.UI.TestimonialsDetails
import digital.upbeat.estisharati_user.ViewHolder.TestimonialsHorizViewHolder

class TestimonialsHorizAdapter(val context: Context, val home: Home, var testimonialsArrayList: ArrayList<String>) : RecyclerView.Adapter<TestimonialsHorizViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestimonialsHorizViewHolder {
        val layoutView = LayoutInflater.from(context).inflate(R.layout.testimonials_horiz_item, parent, false)
        return TestimonialsHorizViewHolder(layoutView)
    }

    override fun getItemCount(): Int {
        return testimonialsArrayList.size
    }

    override fun onBindViewHolder(holder: TestimonialsHorizViewHolder, position: Int) {
        holder.holeLayout.setOnClickListener {
            context.startActivity(Intent(context, TestimonialsDetails::class.java))
        }
    }
}
