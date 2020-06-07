package digital.upbeat.estisharati_user.UI

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import digital.upbeat.estisharati_user.Adapter.MyCoursesAdapter
import digital.upbeat.estisharati_user.Adapter.PackageAdapter
import digital.upbeat.estisharati_user.CarouselHelper.CarouselLayoutManager
import digital.upbeat.estisharati_user.CarouselHelper.CarouselZoomPostLayoutListener
import digital.upbeat.estisharati_user.CarouselHelper.CenterScrollListener
import digital.upbeat.estisharati_user.Helper.HelperMethods
import digital.upbeat.estisharati_user.R
import kotlinx.android.synthetic.main.activity_packages.*

class Packages : AppCompatActivity() {
    lateinit var helperMethods: HelperMethods
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_packages)
        initViews()
        clickEvents()
        InitializeRecyclerview()
    }

    fun initViews() {
        helperMethods = HelperMethods(this@Packages)
    }

    fun clickEvents() {
        nav_back.setOnClickListener { finish() }
        choose_the_package.setOnClickListener {
            startActivity(Intent(this@Packages, PackagesSelection::class.java))
        }
    }

    fun InitializeRecyclerview() {
        val arrayList: ArrayList<String> = arrayListOf()
        arrayList.add("Marketing advice")
        arrayList.add("Legal advice")
        arrayList.add("Administration and business")
        arrayList.add("Finance and Accounting")
        arrayList.add("Health and Fitness")
        val layoutManager = CarouselLayoutManager(CarouselLayoutManager.VERTICAL, false)
        layoutManager.setPostLayoutListener(CarouselZoomPostLayoutListener())
        package_recycler.addOnScrollListener(CenterScrollListener())
        package_recycler.setHasFixedSize(true)
        package_recycler.removeAllViews()
        package_recycler.layoutManager = layoutManager
        package_recycler.adapter = PackageAdapter(this@Packages, this@Packages, arrayList)
    }
}