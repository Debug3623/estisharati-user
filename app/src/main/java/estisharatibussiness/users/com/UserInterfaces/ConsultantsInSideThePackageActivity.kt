package estisharatibussiness.users.com.UserInterfaces

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import estisharatibussiness.users.com.AdapterClasses.ConsultantsInThePackageAdapter
import estisharatibussiness.users.com.DataClassHelperMehtods.Packages.Consultant
import estisharatibussiness.users.com.Helper.HelperMethods
import estisharatibussiness.users.com.R
import estisharatibussiness.users.com.UtilsClasses.BaseCompatActivity
import kotlinx.android.synthetic.main.activity_consultants_in_the_package.*

class ConsultantsInSideThePackageActivity : BaseCompatActivity() {
    lateinit var helperMethods: HelperMethods
    var price = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_consultants_in_the_package)
        initViews()
        clickEvents()
        InitializeRecyclerview()
    }

    fun initViews() {
        helperMethods = HelperMethods(this@ConsultantsInSideThePackageActivity)
        price= intent.getStringExtra("price").toString()
    }

    fun clickEvents() {
        nav_back.setOnClickListener { finish() }
    }

    fun InitializeRecyclerview() {
        val consultantsArrayList = intent.getParcelableArrayListExtra<Consultant>("consultants") as ArrayList<Consultant>
        consultants_in_the_package_recycler.setHasFixedSize(true)
        consultants_in_the_package_recycler.removeAllViews()
        consultants_in_the_package_recycler.layoutManager = LinearLayoutManager(this@ConsultantsInSideThePackageActivity)
        consultants_in_the_package_recycler.adapter = ConsultantsInThePackageAdapter(this@ConsultantsInSideThePackageActivity, price, this@ConsultantsInSideThePackageActivity, consultantsArrayList)
    }



}