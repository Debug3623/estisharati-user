package digital.upbeat.estisharati_user.UI

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import digital.upbeat.estisharati_user.Adapter.MyConsultationsAdapter
import digital.upbeat.estisharati_user.Helper.HelperMethods
import digital.upbeat.estisharati_user.R
import kotlinx.android.synthetic.main.activity_my_consultations.*

class MyConsultations : AppCompatActivity() {
    lateinit var helperMethods: HelperMethods
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_consultations)

        initViews()
    clickEvents()
    InitializeRecyclerview()
}

fun initViews() {
    helperMethods = HelperMethods(this@MyConsultations)
}

fun clickEvents() {
    nav_back.setOnClickListener { finish() }
}

fun InitializeRecyclerview() {
    val arrayList: ArrayList<String> = arrayListOf()
    arrayList.add("Marketing advice")
    arrayList.add("Legal advice")
    arrayList.add("Administration and business")
    arrayList.add("Finance and Accounting")
    arrayList.add("Health and Fitness")
    arrayList.add("Lifestyle")
    arrayList.add("Human Development")
    arrayList.add("Marketing advice")
    arrayList.add("Legal advice")
    arrayList.add("Administration and business")
    arrayList.add("Finance and Accounting")
    arrayList.add("Health and Fitness")
    arrayList.add("Lifestyle")
    arrayList.add("Human Development")
    my_consultations_recycler.setHasFixedSize(true)
    my_consultations_recycler.removeAllViews()
    my_consultations_recycler.layoutManager = LinearLayoutManager(this@MyConsultations)
    my_consultations_recycler.adapter = MyConsultationsAdapter(this@MyConsultations, this@MyConsultations, arrayList)
}
}
