package digital.upbeat.estisharati_user.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import digital.upbeat.estisharati_user.Adapter.ConsultationsAdapter
import digital.upbeat.estisharati_user.Helper.GlobalData
import digital.upbeat.estisharati_user.Helper.HelperMethods
import digital.upbeat.estisharati_user.R
import kotlinx.android.synthetic.main.fragment_consultations.*

/**
 * A simple [Fragment] subclass.
 */
class Consultations : Fragment() {
    lateinit var helperMethods: HelperMethods
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_consultations, container, false)
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

    fun clickEvents() {}
    fun InitializeRecyclerview() {
        if (GlobalData.isThingInitialized()) {
            consultations_recycler.setHasFixedSize(true)
            consultations_recycler.removeAllViews()
            consultations_recycler.layoutManager = LinearLayoutManager(requireContext())
            consultations_recycler.adapter = ConsultationsAdapter(requireContext(), this@Consultations, GlobalData.homeResponse.categories)
            if (GlobalData.homeResponse.categories.size > 0) {
                consultations_recycler.visibility = View.VISIBLE
                emptyLayout.visibility = View.GONE
            } else {
                consultations_recycler.visibility = View.GONE
                emptyLayout.visibility = View.VISIBLE
            }
        } else {
            consultations_recycler.visibility = View.GONE
            emptyLayout.visibility = View.VISIBLE
        }
    }
}
