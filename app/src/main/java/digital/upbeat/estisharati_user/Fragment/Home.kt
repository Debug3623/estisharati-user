package digital.upbeat.estisharati_user.Fragment

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.gson.Gson
import digital.upbeat.estisharati_user.Adapter.ExpConsultationsAdapter
import digital.upbeat.estisharati_user.Adapter.ExpCoursesAdapter
import digital.upbeat.estisharati_user.Adapter.HomePagerAdapter
import digital.upbeat.estisharati_user.Adapter.OnlineUserAdapter
import digital.upbeat.estisharati_user.ApiHelper.RetrofitApiClient
import digital.upbeat.estisharati_user.ApiHelper.RetrofitInterface
import digital.upbeat.estisharati_user.DataClassHelper.DataCallsFireStore
import digital.upbeat.estisharati_user.DataClassHelper.DataUser
import digital.upbeat.estisharati_user.DataClassHelper.DataUserFireStore
import digital.upbeat.estisharati_user.DataClassHelper.Home.Category
import digital.upbeat.estisharati_user.DataClassHelper.Home.HomeResponse
import digital.upbeat.estisharati_user.Helper.GlobalData
import digital.upbeat.estisharati_user.Helper.HelperMethods
import digital.upbeat.estisharati_user.Helper.SharedPreferencesHelper
import digital.upbeat.estisharati_user.R
import digital.upbeat.estisharati_user.UI.*
import kotlinx.android.synthetic.main.app_bar_user_drawer.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.nav_side_manu.*
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class Home(val userDrawer: UserDrawer) : Fragment() {
    lateinit var helperMethods: HelperMethods
    lateinit var preferencesHelper: SharedPreferencesHelper
    lateinit var retrofitInterface: RetrofitInterface
    var homePagerAdapter: HomePagerAdapter? = null
    var count = 0
    private var handler: Handler = Handler()
    val delay: Long = 3000
    lateinit var firestore: FirebaseFirestore
    lateinit var dataUser: DataUser
    lateinit var dataUserFireStore: DataUserFireStore
    var onlineUserListener: ListenerRegistration? = null
    var incomingCallListener: ListenerRegistration? = null
    var runnable: Runnable = object : Runnable {
        override fun run() {
            if (count == homePagerAdapter!!.count - 1) {
                count = 0
            } else {
                count++
            }
            viewpager.setCurrentItem(count, true)
            handler.postDelayed(this, delay.toLong())
        }
    }
    var onlineUserArraylist = arrayListOf<DataUserFireStore>()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        clickEvents()
        if (GlobalData.isThingInitialized()) {
            ShowViewPager()
            InitializeRecyclerview()
            firestoreLisiner()
        } else {
            if (helperMethods.isConnectingToInternet) {
                homeDetailsApiCall()
            } else {
                helperMethods.AlertPopup(getString(R.string.internet_connection_failed), getString(R.string.please_check_your_internet_connection_and_try_again))
            }
        }
    }

    fun initViews() {
        helperMethods = HelperMethods(requireContext())
        preferencesHelper = SharedPreferencesHelper(requireContext())
        retrofitInterface = RetrofitApiClient(GlobalData.BaseUrl).getRetrofit().create(RetrofitInterface::class.java)
        firestore = FirebaseFirestore.getInstance()
        dataUser = preferencesHelper.logInUser
    }

    fun clickEvents() {
        ask_for_advice.setOnClickListener {
            val intent = Intent(requireContext(), LegalAdvice::class.java)
            intent.putExtra("category_id", "")
            intent.putExtra("category_name", "")
            startActivity(intent)
        }
        exp_consultations_all.setOnClickListener {
            userDrawer.navConsultations()
        }
        exp_courses_all.setOnClickListener {
            startActivity(Intent(requireContext(), OnlineCourses::class.java))
        }

    }

    private fun ShowViewPager() {
        homePagerAdapter = HomePagerAdapter(requireContext(), this@Home, GlobalData.homeResponse.slider)
        val rotateimage = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_in)
        viewpager.adapter = homePagerAdapter
        indicator.setViewPager(viewpager)
        viewpager.startAnimation(rotateimage)
    }

    override fun onResume() {
        super.onResume()
        handler.postDelayed(runnable, delay)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(runnable)
    }

    override fun onDestroy() {
        super.onDestroy()
        onlineUserListener?.remove()
        incomingCallListener?.remove()
    }

    fun InitializeRecyclerview() {
        requireActivity().notificationCount.text = GlobalData.homeResponse.notification_count
        val splitCount = GlobalData.homeResponse.categories.size / 2
        var categoriesArrayList1: ArrayList<Category> = arrayListOf()
        var categoriesArrayList2: ArrayList<Category> = arrayListOf()

        if (GlobalData.homeResponse.categories.size > 1) {
            categoriesArrayList1 = ArrayList<Category>(GlobalData.homeResponse.categories.subList(0, splitCount))
            categoriesArrayList2 = ArrayList<Category>(GlobalData.homeResponse.categories.subList(splitCount, GlobalData.homeResponse.categories.size))
        } else {
            categoriesArrayList1 = GlobalData.homeResponse.categories
        }

        exp_consultations_recycler.setHasFixedSize(true)
        exp_consultations_recycler.removeAllViews()
        exp_consultations_recycler.layoutManager = GridLayoutManager(requireContext(), 1, GridLayoutManager.HORIZONTAL, false)
        exp_consultations_recycler.adapter = ExpConsultationsAdapter(requireContext(), this, categoriesArrayList1)

        exp_consultations_recycler1.setHasFixedSize(true)
        exp_consultations_recycler1.removeAllViews()
        exp_consultations_recycler1.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        exp_consultations_recycler1.adapter = ExpConsultationsAdapter(requireContext(), this, categoriesArrayList2)

        exp_courses_recycler.setHasFixedSize(true)
        exp_courses_recycler.removeAllViews()
        exp_courses_recycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        exp_courses_recycler.adapter = ExpCoursesAdapter(requireContext(), this, GlobalData.homeResponse.courses)
    }

    fun homeDetailsApiCall() {
        helperMethods.showProgressDialog("Please wait while loading...")
        val responseBodyCall = retrofitInterface.HOME_API_CALL("Bearer ${dataUser.access_token}")
        responseBodyCall.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                helperMethods.dismissProgressDialog()

                if (response.isSuccessful) {
                    if (response.body() != null) {
                        try {
                            val jsonObject = JSONObject(response.body()!!.string())
                            val status = jsonObject.getString("status")
                            if (status.equals("200")) {
                                val data = jsonObject.getString("data")
                                GlobalData.homeResponse = Gson().fromJson(data, HomeResponse::class.java)
                                ShowViewPager()
                                InitializeRecyclerview()
                                firestoreLisiner()
                            } else {
                                val message = jsonObject.getString("message")
                                helperMethods.AlertPopup("Alert", message)
                            }
                        } catch (e: JSONException) {
                            helperMethods.showToastMessage(getString(R.string.something_went_wrong_on_backend_server))
                            e.printStackTrace()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    } else {
                        Log.d("body", "Body Empty")
                    }
                } else {
                    helperMethods.showToastMessage(getString(R.string.something_went_wrong))
                    Log.d("body", "Not Successful")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                helperMethods.dismissProgressDialog()
                t.printStackTrace()
                helperMethods.AlertPopup("Alert", getString(R.string.your_network_connection_is_slow_please_try_again))
            }
        })
    }

    fun firestoreLisiner() {
        val consultantIdArrayList: ArrayList<String> = arrayListOf()
        for (consultant in GlobalData.homeResponse.consultants) {
            consultantIdArrayList.add(consultant.id)
        }
        //            .whereIn("user_id", consultantIdArrayList)
        onlineUserListener = firestore.collection("Users").orderBy("fname", Query.Direction.ASCENDING).addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            querySnapshot?.let {
                onlineUserArraylist = arrayListOf<DataUserFireStore>()
                for (data in querySnapshot) {
                    val dataUserFireStore = data.toObject(DataUserFireStore::class.java)
                    if (!dataUserFireStore.user_id.equals(dataUser.id) && dataUserFireStore.online_status) {
                        onlineUserArraylist.add(dataUserFireStore)
                    }
                }
                initializeOnlineUserRecyclerview()
            }
        }

        incomingCallListener = firestore.collection("Users").document(dataUser.id).addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
            documentSnapshot?.let {
                dataUserFireStore = documentSnapshot.toObject(DataUserFireStore::class.java)!!
                if (!dataUserFireStore.channel_unique_id.equals("")) {
                    firestore.collection("Calls").document(dataUserFireStore.channel_unique_id).get().addOnSuccessListener {
                        it.let {
                            val dataCallsFireStore = it.toObject(DataCallsFireStore::class.java)!!
                            if (dataCallsFireStore.receiver_id.equals(dataUser.id)) {
                                startActivity(Intent(requireContext(), IncomingCall::class.java))
                            } else {
                            }
                        }
                    }
                }
            }
        }
    }

    fun initializeOnlineUserRecyclerview() {
        if (!onlineUserArraylist.isEmpty()) {
            online_now_layout.visibility = View.VISIBLE
            onlineUserArraylist.add(DataUserFireStore())
            online_user_recycler.setHasFixedSize(true)
            online_user_recycler.removeAllViews()
            online_user_recycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            online_user_recycler.adapter = OnlineUserAdapter(requireContext(), this, onlineUserArraylist)
        } else {
            online_now_layout.visibility = View.GONE
        }
    }
}
