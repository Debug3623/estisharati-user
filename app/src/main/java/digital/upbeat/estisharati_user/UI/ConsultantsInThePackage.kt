package digital.upbeat.estisharati_user.UI

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import digital.upbeat.estisharati_user.Adapter.ConsultantsInThePackageAdapter
import digital.upbeat.estisharati_user.DataClassHelper.Packages.Consultant
import digital.upbeat.estisharati_user.Helper.HelperMethods
import digital.upbeat.estisharati_user.R
import kotlinx.android.synthetic.main.activity_consultants_in_the_package.*

class ConsultantsInThePackage : AppCompatActivity() {
    lateinit var helperMethods: HelperMethods
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_consultants_in_the_package)
        initViews()
        clickEvents()
        InitializeRecyclerview()
    }

    fun initViews() {
        helperMethods = HelperMethods(this@ConsultantsInThePackage)
    }

    fun clickEvents() {
        nav_back.setOnClickListener { finish() }
    }

    fun InitializeRecyclerview() {
        val consultantsArrayList = intent.getParcelableArrayListExtra<Consultant>("consultants") as ArrayList<Consultant>
        consultants_in_the_package_recycler.setHasFixedSize(true)
        consultants_in_the_package_recycler.removeAllViews()
        consultants_in_the_package_recycler.layoutManager = LinearLayoutManager(this@ConsultantsInThePackage)
        consultants_in_the_package_recycler.adapter = ConsultantsInThePackageAdapter(this@ConsultantsInThePackage, this@ConsultantsInThePackage, consultantsArrayList)
    }
}