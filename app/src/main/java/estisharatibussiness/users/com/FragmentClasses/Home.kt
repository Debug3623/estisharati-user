package estisharatibussiness.users.com.FragmentClasses

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.gson.Gson
import estisharatibussiness.users.com.AdapterClasses.*
import estisharatibussiness.users.com.ApiHelperClasses.RetrofitApiClient
import estisharatibussiness.users.com.ApiHelperClasses.RetrofitInterface
import estisharatibussiness.users.com.DataClassHelperMehtods.Chat.DataCallsFireStore
import estisharatibussiness.users.com.DataClassHelperMehtods.Login.DataUser
import estisharatibussiness.users.com.DataClassHelperMehtods.Chat.DataUserFireStore
import estisharatibussiness.users.com.DataClassHelperMehtods.Home.HomeResponse
import estisharatibussiness.users.com.DataClassHelperMehtods.SurveysQuestions.SurveysQuestionsResponse
import estisharatibussiness.users.com.DataClassHelperMehtods.Testimonials.TestimonialsResponse
import estisharatibussiness.users.com.Helper.GlobalData
import estisharatibussiness.users.com.Helper.HelperMethods
import estisharatibussiness.users.com.Helper.SharedPreferencesHelper
import estisharatibussiness.users.com.R
import estisharatibussiness.users.com.UserInterfaces.*
import kotlinx.android.synthetic.main.activity_my_appointment.*
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

class Home(val userDrawerActivity: UserDrawerActivity) : Fragment() {
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
    var allUserListener: ListenerRegistration? = null
    var onlineUserListener: ListenerRegistration? = null
    var incomingCallListener: ListenerRegistration? = null
    var runnable: Runnable = object : Runnable {
        override fun run() {
            homePagerAdapter?.let {
                if (count == it.count - 1) {
                    count = 0
                } else {
                    count++
                }
                viewpager.setCurrentItem(count, true)
            }
            handler.postDelayed(this, delay.toLong())
        }
    }
    var onlineUserArraylist = arrayListOf<DataUserFireStore>()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? { // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        initViews()
        clickEvents()
        if (GlobalData.homeResponseMain != null) {
            ShowViewPager()
            firestoreLisiner()
            InitializeRecyclerview()
        } else {
            if (helperMethods.isConnectingToInternet) {
                homeDetailsApiCall()
            } else {
                helperMethods.AlertPopup(getString(R.string.internet_connection_failed), getString(R.string.please_check_your_internet_connection_and_try_again))
            }
        }
        Log.e("access_token", preferencesHelper.logInUser.access_token)
        swipeRefreshLayout.setOnRefreshListener {
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
        Log.d("BaseUrl1", GlobalData.BaseUrl)

        retrofitInterface = RetrofitApiClient(GlobalData.BaseUrl).getRetrofit().create(RetrofitInterface::class.java)
        firestore = FirebaseFirestore.getInstance()
        dataUser = preferencesHelper.logInUser

        if (!GlobalData.courseId.equals("")) {
            val intent = Intent(context, ActivityCourseDetails::class.java)
            intent.putExtra("courseId", GlobalData.courseId)
            startActivity(intent)
            GlobalData.courseId = ""
        }
        if (!GlobalData.surveyId.equals("")) {
            val intent = Intent(context, ActivitySurvey::class.java)
            intent.putExtra("survey_id", GlobalData.surveyId)
            startActivity(intent)
            GlobalData.surveyId = ""
        }

    }

    @SuppressLint("MissingInflatedId") fun clickEvents() {
        ask_for_advice.setOnClickListener {
            val intent = Intent(requireContext(), ActivityLegalAdvice::class.java)
            intent.putExtra("category_id", "")
            intent.putExtra("category_name", "")
            startActivity(intent)

        }
        exp_consultations_all.setOnClickListener {
            userDrawerActivity.navConsultations()
        }
        exp_courses_all.setOnClickListener {
            startActivity(Intent(requireContext(), OnlineCourses::class.java))
        }
        testimonials_all.setOnClickListener {
            startActivity(Intent(requireContext(), ActivityTestimonials::class.java))
        }
        packageImage.setOnClickListener {
            val intent = Intent(requireContext(), ActivityPackages::class.java)
            intent.putExtra("viaFrom", "Home")
            startActivity(intent)
        }
        surveyImage.setOnClickListener {
            startActivity(Intent(requireContext(), ActivitySurveyList::class.java))
        }
    }

    private fun congratulationDialogue() {
        val  dialogBuilder = context?.let { AlertDialog.Builder(it) }
        val layoutView = layoutInflater.inflate(R.layout.congratulation_file, null)
        val dialogButton = layoutView.findViewById(R.id.btnDialog) as Button
        val dialogButtonRead = layoutView.findViewById(R.id.btnDialogRead) as Button
        dialogBuilder?.setView(layoutView)
        val   alertDialog = dialogBuilder?.create()
        alertDialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()
        dialogButton.setOnClickListener(View.OnClickListener { alertDialog?.dismiss() })

        dialogButtonRead.setOnClickListener(View.OnClickListener {
            alertDialog?.dismiss()
            val intent = Intent(context, ActivityCourseDetails::class.java)
            intent.putExtra("courseId", "77")
            context?.startActivity(intent)
        })

    }


    private fun ShowViewPager() {
        requireActivity().notificationCount.text = GlobalData.homeResponse.notification_count

        homePagerAdapter = HomePagerAdapter(requireContext(), this@Home, GlobalData.homeResponse.slider)
        val rotateimage = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_in)
        viewpager.adapter = homePagerAdapter
        indicator.setViewPager(viewpager)
        viewpager.startAnimation(rotateimage)
    }

    override fun onStart() {
        GlobalData.homeResponseMain?.let {
            requireActivity().notificationCount.text = it.notification_count
        }

        if (GlobalData.testimonialsResponse == null) {
            if (helperMethods.isConnectingToInternet) {
                experiencApiCall()
            } else {
                helperMethods.AlertPopup(getString(R.string.internet_connection_failed), getString(R.string.please_check_your_internet_connection_and_try_again))
            }
        } else {
            initializeTestimonialsRecyclerview()
        }




        super.onStart()
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
        allUserListener?.remove()
    }

    fun InitializeRecyclerview() {
        sideNavBackgroundColorBasedOnPackage()
        //        val splitCount = GlobalData.homeResponse.categories.size / 2
        //        var categoriesArrayList1: ArrayList<Category> = arrayListOf()
        //        var categoriesArrayList2: ArrayList<Category> = arrayListOf()
        //
        //        if (GlobalData.homeResponse.categories.size > 1) {
        //            categoriesArrayList1 = ArrayList<Category>(GlobalData.homeResponse.categories.subList(0, splitCount))
        //            categoriesArrayList2 = ArrayList<Category>(GlobalData.homeResponse.categories.subList(splitCount, GlobalData.homeResponse.categories.size))
        //        } else {
        //            categoriesArrayList1 = GlobalData.homeResponse.categories
        //        }
        //
        //        exp_consultations_recycler.setHasFixedSize(true)
        //        exp_consultations_recycler.removeAllViews()
        //        exp_consultations_recycler.layoutManager = GridLayoutManager(requireContext(), 1, GridLayoutManager.HORIZONTAL, false)
        //        exp_consultations_recycler.adapter = ExpConsultationsAdapter(requireContext(), this, categoriesArrayList1)
        //
        //        exp_consultations_recycler1.setHasFixedSize(true)
        //        exp_consultations_recycler1.removeAllViews()
        //        exp_consultations_recycler1.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        //        exp_consultations_recycler1.adapter = ExpConsultationsAdapter(requireContext(), this, categoriesArrayList2)
        exp_courses_recycler.setHasFixedSize(true)
        exp_courses_recycler.removeAllViews()
        exp_courses_recycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        exp_courses_recycler.adapter = ExpCoursesAdapter(requireContext(), this, GlobalData.homeResponse.courses)
        if (GlobalData.homeResponse.courses.isEmpty()) {
            courseLayout.visibility = View.GONE
        } else {
            courseLayout.visibility = View.VISIBLE
        }
    }

    fun sideNavBackgroundColorBasedOnPackage() {
        var subscriptionsPrice = 0.0
        for (subscriptions in GlobalData.homeResponse.subscriptions) {
            if (subscriptions.price.toDouble() > subscriptionsPrice) {
                subscriptionsPrice = subscriptions.price.toDouble()
                requireActivity().sideNavBack.setBackgroundColor(Color.parseColor(subscriptions.color_code))
            }
        }
    }

    fun homeDetailsApiCall() {
        helperMethods.showProgressDialog(getString(R.string.please_wait_while_loading))
        val responseBodyCall = retrofitInterface.HOME_API_CALL("Bearer ${dataUser.access_token}", GlobalData.FcmToken)
        responseBodyCall.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                helperMethods.dismissProgressDialog()
                swipeRefreshLayout.isRefreshing = false
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        try {
                            val jsonObject = JSONObject(response.body()!!.string())
                            val status = jsonObject.getString("status")
                            if (status.equals("200")) {
                                val data = jsonObject.getString("data")
                                GlobalData.homeResponse = Gson().fromJson(data, HomeResponse::class.java)
                                GlobalData.homeResponseMain = GlobalData.homeResponse
                                Log.d("notifications_count",  GlobalData.homeResponseMain!!.notification_count)
                                Log.d("alert_status",  GlobalData.homeResponseMain!!.alert_status.toString())
                                ShowViewPager()
                                InitializeRecyclerview()
                                firestoreLisiner()
                                alertApiCall()
                                if (GlobalData.homeResponseMain!!.alert_status){
                                    Log.d("no Alert","user Already register")
                                }else{
                                    congratulationDialogue()

                                    Log.d("Alert time","congratulation time to display")

                                }

                            } else {
                                val message = jsonObject.getString("message")
                                if (helperMethods.checkTokenValidation(status, message)) {
                                    requireActivity().finish()
                                    return
                                }
                                helperMethods.AlertPopup(getString(R.string.alert), message)
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
                swipeRefreshLayout.isRefreshing = false
                t.printStackTrace()
                helperMethods.AlertPopup(getString(R.string.alert), getString(R.string.your_network_connection_is_slow_please_try_again))
            }
        })
    }

    fun firestoreLisiner() {
        val consultantIdArrayList: ArrayList<String> = arrayListOf()
        for (consultant in GlobalData.homeResponse.consultants) {
            consultantIdArrayList.add(consultant.id)
        }
        Log.d("consultantIdArrayList", consultantIdArrayList.toString()) //        if (GlobalData.homeResponse.consultants.size > 0) {
        //            .whereIn("user_id", consultantIdArrayList)
        onlineUserListener = firestore.collection("Users").orderBy("fname", Query.Direction.ASCENDING).addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            querySnapshot?.let {
                onlineUserArraylist = arrayListOf<DataUserFireStore>()
                for (data in querySnapshot) {
                    val dataUserFireStore = data.toObject(DataUserFireStore::class.java)
                    if (!dataUserFireStore.user_id.equals(dataUser.id) && dataUserFireStore.online_status) {
                        if (dataUserFireStore.user_type.equals("user") || helperMethods.findConsultantId(dataUserFireStore.user_id)) {
                            onlineUserArraylist.add(dataUserFireStore)
                            Log.d("online_users_list","${onlineUserArraylist.size}")
                        }
                    }
                }
                initializeOnlineUserRecyclerview()
            }
        } //        } else {
        //            initializeOnlineUserRecyclerview()
        //        }
        incomingCallListener = firestore.collection("Users").document(dataUser.id).addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
            documentSnapshot?.let {
                dataUserFireStore = documentSnapshot.toObject(DataUserFireStore::class.java)!!
                if (!dataUserFireStore.channel_unique_id.equals("")) {
                    firestore.collection("Calls").document(dataUserFireStore.channel_unique_id).get().addOnSuccessListener {
                        it.let {
                            val dataCallsFireStore = it.toObject(DataCallsFireStore::class.java)!!
                            if (dataCallsFireStore.receiver_id.equals(dataUser.id)) {
                                startActivity(Intent(requireContext(), ActivityIncomingCall::class.java))
                            } else {
                            }
                        }
                    }
                }
            }
        }

        allUserListener = firestore.collection("Users").orderBy("fname", Query.Direction.ASCENDING).addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            querySnapshot?.let {
                GlobalData.allUserArraylist = arrayListOf<DataUserFireStore>()
                for (data in querySnapshot) {
                    val dataUserFireStore = data.toObject(DataUserFireStore::class.java)
                    GlobalData.allUserArraylist.add(dataUserFireStore)
                }
            }
        }
    }

    private fun initializeOnlineUserRecyclerview() {
        if (onlineUserArraylist.isNotEmpty()) {
            online_now_layout.visibility = View.VISIBLE
            onlineUserArraylist.add(DataUserFireStore())
            online_user_recycler.setHasFixedSize(true)
            online_user_recycler.removeAllViews()
            online_user_recycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            online_user_recycler.adapter = OnlineUserAdapter(requireContext(), this@Home, onlineUserArraylist)
        } else {
            online_now_layout.visibility = View.GONE
        }
    }

    fun initializeTestimonialsRecyclerview() {
        GlobalData.testimonialsResponse?.let {
            if (!it.data.isEmpty()) {
                testimonialsHeaderLayout.visibility = View.VISIBLE
                testimonialsHorizRecycler.visibility = View.VISIBLE
                testimonialsHorizRecycler.setHasFixedSize(true)
                testimonialsHorizRecycler.removeAllViews()
                testimonialsHorizRecycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                testimonialsHorizRecycler.adapter = TestimonialsHorizAdapter(requireContext(), this@Home, it.data)
            } else {
                testimonialsHeaderLayout.visibility = View.GONE
                testimonialsHorizRecycler.visibility = View.GONE
            }
        }
    }

    fun experiencApiCall() {
        helperMethods.showProgressDialog(getString(R.string.please_wait_while_loading))
        val responseBodyCall = retrofitInterface.GET_SHARE_EXPERIENCE_API_CALL("Bearer ${dataUser.access_token}")
        responseBodyCall.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                helperMethods.dismissProgressDialog()

                if (response.isSuccessful) {
                    if (response.body() != null) {
                        try {
                            GlobalData.testimonialsResponse = Gson().fromJson(response.body()!!.string(), TestimonialsResponse::class.java)
                            if (GlobalData.testimonialsResponse?.status.equals("200")) {
                                initializeTestimonialsRecyclerview()
                            } else {
                                if (helperMethods.checkTokenValidation(GlobalData.testimonialsResponse!!.status, GlobalData.testimonialsResponse!!.message)) {
                                    requireActivity().finish()
                                    return
                                }
                                helperMethods.AlertPopup(getString(R.string.alert), GlobalData.testimonialsResponse!!.message)
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
                helperMethods.AlertPopup(getString(R.string.alert), getString(R.string.your_network_connection_is_slow_please_try_again))
            }
        })
    }

    fun alertApiCall() {
        val responseBodyCall = retrofitInterface.STATUS_ALERT("Bearer ${dataUser.access_token}")
        responseBodyCall.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                if (response.isSuccessful) {

                     Log.d("status_done_api_call","true")

                } else {
                    helperMethods.showToastMessage(getString(R.string.something_went_wrong))
                    Log.d("body", "Not Successful")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                helperMethods.dismissProgressDialog()
                t.printStackTrace()
                helperMethods.AlertPopup(getString(R.string.alert), getString(R.string.your_network_connection_is_slow_please_try_again))
            }
        })
    }


}

