package digital.upbeat.estisharati_user.UI

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import digital.upbeat.estisharati_user.Adapter.LegalAdviceAdapter
import digital.upbeat.estisharati_user.Helper.HelperMethods
import digital.upbeat.estisharati_user.R
import kotlinx.android.synthetic.main.activity_legal_advice.*

class LegalAdvice : AppCompatActivity() {
    lateinit var helperMethods: HelperMethods
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_legal_advice)

        initViews()
        clickEvents()
        InitializeRecyclerview()
    }

    fun initViews() {
        helperMethods = HelperMethods(this@LegalAdvice)
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
        arrayList.add("Marketing advice")
        arrayList.add("Legal advice")
        arrayList.add("Administration and business")
        arrayList.add("Finance and Accounting")
        arrayList.add("Health and Fitness")

        legal_advice_recycler.setHasFixedSize(true)
        legal_advice_recycler.removeAllViews()
        legal_advice_recycler.layoutManager = LinearLayoutManager(this@LegalAdvice)
        legal_advice_recycler.adapter = LegalAdviceAdapter(this@LegalAdvice, this@LegalAdvice, arrayList)
    }
}
