package estisharatibussiness.users.com.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import estisharatibussiness.users.com.DataClassHelper.SurveyList.Data
import estisharatibussiness.users.com.R
import estisharatibussiness.users.com.UserInterface.ActivitySurvey
import estisharatibussiness.users.com.UserInterface.ActivitySurveyList
import estisharatibussiness.users.com.ViewHolder.SurveyListViewHolder

class SurveyListAdapter(val context: Context, val activitySurveyList: ActivitySurveyList, val surveyArrayList: ArrayList<Data>) : RecyclerView.Adapter<SurveyListViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SurveyListViewHolder {
        val layoutView = LayoutInflater.from(context).inflate(R.layout.survey_item, parent, false)
        return SurveyListViewHolder(layoutView)
    }

    override fun onBindViewHolder(holder: SurveyListViewHolder, position: Int) {
        Glide.with(context).load(surveyArrayList.get(position).image_path).apply(activitySurveyList.helperMethods.profileRequestOption).into(holder.surveyImage)
        holder.surveyName.text = surveyArrayList.get(position).title
        holder.surveyDescription.text = activitySurveyList.helperMethods.getHtmlText(surveyArrayList.get(position).description)
        holder.surveyLayout.setOnClickListener {
            val intent = Intent(context, ActivitySurvey::class.java)
            intent.putExtra("survey_id", surveyArrayList.get(position).id)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return surveyArrayList.size
    }
}