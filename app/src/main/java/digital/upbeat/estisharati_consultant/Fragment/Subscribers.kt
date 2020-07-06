package digital.upbeat.estisharati_consultant.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import digital.upbeat.estisharati_consultant.Adapter.SubscribersAdapter
import digital.upbeat.estisharati_consultant.Helper.HelperMethods
import digital.upbeat.estisharati_consultant.Helper.SharedPreferencesHelper
import digital.upbeat.estisharati_consultant.R
import digital.upbeat.estisharati_user.DataClassHelper.DataUser
import kotlinx.android.synthetic.main.fragment_subscribers.*

class Subscribers : Fragment() {
    lateinit var helperMethods: HelperMethods
    lateinit var preferencesHelper: SharedPreferencesHelper
    lateinit var dataUser: DataUser
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_subscribers, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initializeRecyclerview()
    }

    fun initViews() {
        helperMethods = HelperMethods(requireContext())
        preferencesHelper = SharedPreferencesHelper(requireContext())
        dataUser = preferencesHelper.logInConsultant
    }

    fun initializeRecyclerview() {
        val arrayList: ArrayList<String> = arrayListOf()

        arrayList.add("Lifestyle")
        arrayList.add("Health & fitness")
        arrayList.add("Finance and Accounting")
        arrayList.add("Money & business")
        arrayList.add("Law")
        arrayList.add("Marketing")
        arrayList.add("Design")
        arrayList.add("Human Development")
        arrayList.add("Lifestyle")
        arrayList.add("Health & fitness")

        subscribers_recycler.setHasFixedSize(true)
        subscribers_recycler.removeAllViews()
        subscribers_recycler.layoutManager = LinearLayoutManager(requireContext())
        subscribers_recycler.adapter = SubscribersAdapter(requireContext(), this, arrayList)
    }
}