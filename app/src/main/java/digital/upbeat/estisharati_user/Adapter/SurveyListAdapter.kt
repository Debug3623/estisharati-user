package digital.upbeat.estisharati_user.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import digital.upbeat.estisharati_user.DataClassHelper.SurveyList.Data
import digital.upbeat.estisharati_user.R
import digital.upbeat.estisharati_user.UI.Survey
import digital.upbeat.estisharati_user.UI.SurveyList
import digital.upbeat.estisharati_user.ViewHolder.SurveyListViewHolder

class SurveyListAdapter(val context: Context, val surveyList: SurveyList, val surveyArrayList: ArrayList<Data>) : RecyclerView.Adapter<SurveyListViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SurveyListViewHolder {
        val layoutView = LayoutInflater.from(context).inflate(R.layout.survey_item, parent, false)
        return SurveyListViewHolder(layoutView)
    }

    override fun onBindViewHolder(holder: SurveyListViewHolder, position: Int) {
        Glide.with(context).load(surveyArrayList.get(position).image_path).apply(surveyList.helperMethods.profileRequestOption).into(holder.surveyImage)
        holder.surveyName.text = surveyArrayList.get(position).title
        holder.surveyDescription.text = surveyList.helperMethods.getHtmlText(surveyArrayList.get(position).description)
        holder.surveyLayout.setOnClickListener {
            val intent = Intent(context, Survey::class.java);
            intent.putExtra("survey_id", surveyArrayList.get(position).id)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return surveyArrayList.size
    }
}