package estisharatibussiness.users.com.ViewHolder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.survey_item.view.*

class SurveyListViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {

val surveyImage=itemView.surveyImage
val surveyName=itemView.surveyName
val surveyDescription=itemView.surveyDescription
val surveyLayout=itemView.surveyLayout
}