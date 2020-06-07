package digital.upbeat.estisharati_user.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import digital.upbeat.estisharati_user.Adapter.DocumentAdapter
import digital.upbeat.estisharati_user.Helper.HelperMethods
import digital.upbeat.estisharati_user.R
import kotlinx.android.synthetic.main.fragment_documents.*

class Documents : Fragment() {
    lateinit var helperMethods: HelperMethods
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_documents, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        clickEvents()
        InitializeRecyclerview()
    }

    fun initViews() {
        helperMethods = HelperMethods(requireContext())
    }

    fun clickEvents() {
    }

    fun InitializeRecyclerview() {
        val arrayList: ArrayList<String> = arrayListOf()
        arrayList.add("Marketing advice")
        arrayList.add("Legal advice")
        arrayList.add("Administration and business")
        arrayList.add("Finance and Accounting")

        document_recycler.setHasFixedSize(true)
        document_recycler.removeAllViews()
        document_recycler.layoutManager = LinearLayoutManager(requireContext())
        document_recycler.adapter = DocumentAdapter(requireContext(), this@Documents, arrayList)
    }
}