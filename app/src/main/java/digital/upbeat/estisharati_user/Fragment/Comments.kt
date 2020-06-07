package digital.upbeat.estisharati_user.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import digital.upbeat.estisharati_user.Adapter.ConsultantCommentsReplyAdapter
import digital.upbeat.estisharati_user.Helper.HelperMethods
import digital.upbeat.estisharati_user.R
import kotlinx.android.synthetic.main.fragment_comments.*

class Comments : Fragment() {
    lateinit var helperMethods: HelperMethods
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_comments, container, false)
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


        course_comments_reply_recycler.setHasFixedSize(true)
        course_comments_reply_recycler.removeAllViews()
        course_comments_reply_recycler.layoutManager = LinearLayoutManager(requireContext())
        course_comments_reply_recycler.adapter = ConsultantCommentsReplyAdapter(requireContext(), null, arrayList)
    }
}