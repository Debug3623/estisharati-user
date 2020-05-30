package digital.upbeat.estisharati_user.UI

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import digital.upbeat.estisharati_user.Adapter.ConsultantCommentsReplyAdapter
import digital.upbeat.estisharati_user.Helper.HelperMethods
import digital.upbeat.estisharati_user.R
import digital.upbeat.estisharati_user.Utils.BlurTransformation
import kotlinx.android.synthetic.main.activity_consultant_details.*

class ConsultantDetails : AppCompatActivity() {
    lateinit var helperMethods: HelperMethods
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_consultant_details)
        initViews()
        clickEvents()
        InitializeRecyclerview()
    }

    fun initViews() {
        helperMethods = HelperMethods(this@ConsultantDetails)
    }

    fun clickEvents() {
        nav_back.setOnClickListener { finish() }
    }

    fun InitializeRecyclerview() {
        val arrayList: ArrayList<String> = arrayListOf()
        arrayList.add("Marketing advice")
        arrayList.add("Legal advice")
        arrayList.add("Administration and business")


        consultant_comments_reply_recycler.setHasFixedSize(true)
        consultant_comments_reply_recycler.removeAllViews()
        consultant_comments_reply_recycler.layoutManager = LinearLayoutManager(this@ConsultantDetails)
        consultant_comments_reply_recycler.adapter = ConsultantCommentsReplyAdapter(this@ConsultantDetails, this@ConsultantDetails, arrayList)
    }
}