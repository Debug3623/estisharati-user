package digital.upbeat.estisharati_consultant.UI

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import androidx.recyclerview.widget.LinearLayoutManager
import digital.upbeat.estisharati_consultant.Adapter.ChatAdapter
import digital.upbeat.estisharati_consultant.Helper.HelperMethods
import digital.upbeat.estisharati_consultant.Helper.SharedPreferencesHelper
import digital.upbeat.estisharati_consultant.R
import digital.upbeat.estisharati_user.DataClassHelper.DataMessageFireStore
import digital.upbeat.estisharati_user.DataClassHelper.DataUser
import digital.upbeat.estisharati_user.DataClassHelper.DataUserFireStore
import digital.upbeat.estisharati_user.DataClassHelper.DataUserMessageFireStore
import kotlinx.android.synthetic.main.activity_chat_page.*

class ChatPage : AppCompatActivity() {
    lateinit var helperMethods: HelperMethods
    lateinit var preferencesHelper: SharedPreferencesHelper
    lateinit var dataUser: DataUser
    private var chatAdapter: ChatAdapter? = null
    private var recyclerChatViewState: Parcelable? = null
    val messagesArrayList = arrayListOf<DataMessageFireStore>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_page)
        initViews()
        clickEvents()
        InitializeRecyclerview()
    }

    fun clickEvents() {
        nav_back.setOnClickListener { finish() }
    }

    fun initViews() {
        helperMethods = HelperMethods(this@ChatPage)
        preferencesHelper = SharedPreferencesHelper(this@ChatPage)
        dataUser = preferencesHelper.logInConsultant
    }

    fun InitializeRecyclerview() {
        messagesArrayList.add(DataMessageFireStore())
        messagesArrayList.add(DataMessageFireStore())
        messagesArrayList.add(DataMessageFireStore())
        messagesArrayList.add(DataMessageFireStore())
        messagesArrayList.add(DataMessageFireStore())
        messagesArrayList.add(DataMessageFireStore())

        chatAdapter?.let {
            chat_recycler.layoutManager?.let {
                recyclerChatViewState = it.onSaveInstanceState()
            }
        }
        chat_recycler.setHasFixedSize(true)
        chat_recycler.removeAllViews()
        chat_recycler.layoutManager = LinearLayoutManager(this@ChatPage)
        chatAdapter = ChatAdapter(this@ChatPage, this@ChatPage, messagesArrayList)
        chat_recycler.adapter = chatAdapter
        chat_recycler.scrollToPosition(messagesArrayList.size - 1)
        recyclerChatViewState?.let {
            chat_recycler.layoutManager?.onRestoreInstanceState(recyclerChatViewState)
        }
    }
}