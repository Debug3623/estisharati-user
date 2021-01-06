package digital.upbeat.estisharati_consultant.Fragment

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.gson.Gson
import digital.upbeat.estisharati_consultant.Adapter.SubscribersAdapter
import digital.upbeat.estisharati_consultant.ApiHelper.RetrofitApiClient
import digital.upbeat.estisharati_consultant.ApiHelper.RetrofitInterface
import digital.upbeat.estisharati_consultant.DataClassHelper.*
import digital.upbeat.estisharati_consultant.DataClassHelper.MySubsribers.MySubscriberResponse
import digital.upbeat.estisharati_consultant.Helper.GlobalData
import digital.upbeat.estisharati_consultant.Helper.HelperMethods
import digital.upbeat.estisharati_consultant.Helper.SharedPreferencesHelper
import digital.upbeat.estisharati_consultant.R
import digital.upbeat.estisharati_consultant.UI.IncomingCall
import kotlinx.android.synthetic.main.fragment_subscribers.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class Subscribers : Fragment() {
    lateinit var helperMethods: HelperMethods
    lateinit var preferencesHelper: SharedPreferencesHelper
    lateinit var retrofitInterface: RetrofitInterface
    lateinit var firestore: FirebaseFirestore
    lateinit var dataUser: DataUser
    var onlineConsultationArraylist = arrayListOf<DataUserFireStore>()
    var userArraylist = arrayListOf<DataUserFireStore>()
    val dataUserMessageFireStoreArrayList = arrayListOf<DataUserMessageFireStore>()
    var UserListener: ListenerRegistration? = null
    var ChatListener1: ListenerRegistration? = null
    var ChatListener2: ListenerRegistration? = null
    var incomingCallListener: ListenerRegistration? = null
    private var subscribersAdapter: SubscribersAdapter? = null
    private var recyclerRecentChatViewState: Parcelable? = null
    lateinit var mySubscriberResponse: MySubscriberResponse
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_subscribers, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        clickEvents()
        mySubsribersApiCall()
    }

    fun initViews() {
        helperMethods = HelperMethods(requireContext())
        preferencesHelper = SharedPreferencesHelper(requireContext())
        firestore = FirebaseFirestore.getInstance()
        dataUser = preferencesHelper.logInConsultant
        retrofitInterface = RetrofitApiClient(GlobalData.BaseUrl).getRetrofit().create(RetrofitInterface::class.java)
    }

    fun clickEvents() {
        forward_cancel.setOnClickListener {
            GlobalData.forwardType = ""
            GlobalData.forwardContent = ""
            forward_layout.visibility = View.GONE
            helperMethods.showToastMessage("Forward cancel !")
        }

        incomingCallListener = firestore.collection("Users").document(dataUser.id).addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
            documentSnapshot?.let {
                val dataUserFireStore = documentSnapshot.toObject(DataUserFireStore::class.java)!!
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

    fun onlineConsultationLisiner() {
        val userIds: ArrayList<String> = arrayListOf()
        for (User in mySubscriberResponse.data) {
            userIds.add(User.user_id)
        }
        ChatListener1 = firestore.collection("Chats").whereEqualTo("sender_id", dataUser.id).addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            recentChatLisiner()
        }
        ChatListener2 = firestore.collection("Chats").whereEqualTo("receiver_id", dataUser.id).addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            recentChatLisiner()
        }
        if (userIds.size > 0) {
            UserListener = firestore.collection("Users").whereIn("user_id", userIds).orderBy("fname", Query.Direction.ASCENDING).addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                querySnapshot?.let {
                    onlineConsultationArraylist.clear()
                    userArraylist.clear()
                    for (data in querySnapshot) {
                        val dataUserFireStore = data.toObject(DataUserFireStore::class.java)
                        if (!dataUserFireStore.user_id.equals(dataUser.id)) {
                            if (dataUserFireStore.online_status) {
                                onlineConsultationArraylist.add(dataUserFireStore)
                            }
                            userArraylist.add(dataUserFireStore)
                        }
                    }
                    recentChatLisiner()
                }
            }
        } else {
            recentChatRecycler()
        }
    }

    fun recentChatLisiner() {
        dataUserMessageFireStoreArrayList.clear()
        var userCount = 0
        for (data in userArraylist) {
            val IdArray = arrayListOf<Int>()
            IdArray.add(dataUser.id.toInt())
            IdArray.add(data.user_id.toInt())
            Collections.sort(IdArray)
            firestore.collection("Chats").whereEqualTo("communication_id", IdArray).orderBy("send_time", Query.Direction.ASCENDING).limitToLast(51).get().addOnSuccessListener {
                val messagesArrayList = arrayListOf<DataMessageFireStore>()
                it?.let {
                    for (dataMsg in it) {
                        val messageFireStore = dataMsg.toObject(DataMessageFireStore::class.java)
                        messagesArrayList.add(messageFireStore)
                        if (messageFireStore.receiver_id.equals(dataUser.id)) {
                            if (messageFireStore.message_status.equals("send")) {
                                val hashMap = hashMapOf<String, Any>("message_status" to "delivered")
                                firestore.collection("Chats").document(dataMsg.id).update(hashMap).addOnFailureListener {
                                    Log.d("FailureListener", "" + it.localizedMessage)
                                }
                            }
                        }
                    }
                }

                if (helperMethods.containsUserIdForChat(dataUserMessageFireStoreArrayList, data.user_id)) {
                    dataUserMessageFireStoreArrayList.add(DataUserMessageFireStore(data, messagesArrayList))
                }
                userCount++
                Log.d("FailureCheck", "" + userArraylist.size + "     " + userCount)
                if (userCount == userArraylist.size) {
                    recentChatRecycler()
                }
            }
        }
    }

    fun recentChatRecycler() {
        if (shimmer_view_container.visibility == View.VISIBLE) {
            shimmer_view_container.stopShimmer()
            shimmer_view_container.visibility = View.GONE
            subscribers_recent_chat_recycler.visibility = View.VISIBLE
        }
        sortArraylistDateWise()
        subscribersAdapter?.let {
            subscribers_recent_chat_recycler.layoutManager?.let {
                recyclerRecentChatViewState = it.onSaveInstanceState()
            }
        }
        subscribers_recent_chat_recycler.setHasFixedSize(true)
        subscribers_recent_chat_recycler.removeAllViews()
        subscribersAdapter = SubscribersAdapter(requireContext(), this@Subscribers, dataUserMessageFireStoreArrayList)
        subscribers_recent_chat_recycler.adapter = subscribersAdapter
        subscribers_recent_chat_recycler.layoutManager = LinearLayoutManager(requireContext())
        recyclerRecentChatViewState?.let {
            subscribers_recent_chat_recycler.layoutManager?.onRestoreInstanceState(recyclerRecentChatViewState)
        }
    }

    fun sortArraylistDateWise() {
        Collections.sort(dataUserMessageFireStoreArrayList, object : Comparator<DataUserMessageFireStore> {
            override fun compare(dataUserMessageFireStore1: DataUserMessageFireStore, dataUserMessageFireStore2: DataUserMessageFireStore): Int {
                if (dataUserMessageFireStore1.messagesArrayList.size > 0) {
                    if (dataUserMessageFireStore1.messagesArrayList.get(dataUserMessageFireStore1.messagesArrayList.lastIndex).send_time == null || dataUserMessageFireStore2.messagesArrayList.get(dataUserMessageFireStore2.messagesArrayList.lastIndex).send_time == null) {
                        return 0
                    } else {
                        return dataUserMessageFireStore1.messagesArrayList.get(dataUserMessageFireStore1.messagesArrayList.lastIndex).send_time.compareTo(dataUserMessageFireStore2.messagesArrayList.get(dataUserMessageFireStore2.messagesArrayList.lastIndex).send_time)
                    }
                } else {
                    return 0
                }
            }
        })
        Collections.reverse(dataUserMessageFireStoreArrayList)
    }

    override fun onStart() {
        super.onStart()
        if (!GlobalData.forwardContent.isEmpty()) {
            forward_layout.visibility = View.VISIBLE
        }
    }

    override fun onStop() {
        super.onStop()
        forward_layout.visibility = View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        UserListener?.remove()
        ChatListener1?.remove()
        ChatListener2?.remove()
    }

    fun mySubsribersApiCall() {
        shimmer_view_container.startShimmer()
        shimmer_view_container.visibility = View.VISIBLE
        subscribers_recent_chat_recycler.visibility = View.GONE
        helperMethods.showProgressDialog("Please wait while loading...")
        val responseBodyCall = retrofitInterface.MY_SUBSRIBERS_API_CALL("Bearer ${dataUser.access_token}")
        responseBodyCall.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                helperMethods.dismissProgressDialog()

                if (response.isSuccessful) {
                    if (response.body() != null) {
                        try {
                            mySubscriberResponse = Gson().fromJson(response.body()!!.string(), MySubscriberResponse::class.java)
                            if (mySubscriberResponse.status.equals("200")) {
                                onlineConsultationLisiner()
                            } else {
                                val message = JSONObject(response.body()!!.string()).getString("message")
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
}