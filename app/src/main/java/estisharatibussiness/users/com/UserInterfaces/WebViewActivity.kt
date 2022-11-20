package estisharatibussiness.users.com.UserInterfaces

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import estisharatibussiness.users.com.Helper.HelperMethods
import estisharatibussiness.users.com.R
import estisharatibussiness.users.com.UtilsClasses.BaseCompatActivity
import kotlinx.android.synthetic.main.activity_web_view.*
import kotlinx.android.synthetic.main.no_internet_page.*

class WebViewActivity : BaseCompatActivity() {
    lateinit var helperMethods: HelperMethods
    var url = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)
        initViews()
        clickEvents()
        checkInternetConnection()
    }

    fun initViews() {
        helperMethods = HelperMethods(this@WebViewActivity)
        if (intent.extras != null) {
            action_bar_title.text = intent.getStringExtra("name")

//            url = "${intent.getStringExtra("url")}" // for video only
//            url = "https://docs.google.com/viewer?url=https://www.antennahouse.com/XSLsample/pdf/sample-link_1.pdf &embedded=true"
//            url = "https://docs.google.com/viewer?url=${intent.getStringExtra("url")}&embedded=true"

            url = "https://docs.google.com/viewer?embedded=true&url=${intent.getStringExtra("url")}"
            Log.d("urlWhole:", "https://docs.google.com/viewer?url=${intent.getStringExtra("url")}&embedded=true")
        }
    }

    fun clickEvents() {
        nav_back.setOnClickListener { finish() }
        retry_internet.setOnClickListener { checkInternetConnection() }
    }

    fun checkInternetConnection() {
        if (helperMethods.isConnectingToInternet) {
            web_view.visibility = View.VISIBLE
            no_internet_page.visibility = View.GONE
            showWebview()
        } else {
            no_internet_page.visibility = View.VISIBLE
            web_view.visibility = View.GONE
        }
    }

    fun showWebview() {
        helperMethods.showProgressDialog(getString(R.string.please_wait_while_loading))
        web_view.settings.javaScriptEnabled = true
        web_view.webViewClient = WebViewController(helperMethods)
        web_view.loadUrl(url)
    }

    class WebViewController(val helperMethods: HelperMethods) : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            view.loadUrl(url)
            return true
        }

        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            view.loadUrl("javascript:(function() { " + "document.getElementsByClassName('ndfHFb-c4YZDc-GSQQnc-LgbsSe ndfHFb-c4YZDc-to915-LgbsSe VIpgJd-TzA9Ye-eEGnhe ndfHFb-c4YZDc-LgbsSe')[0].style.display='none'; })()")
            helperMethods.dismissProgressDialog()
        }

        override fun onReceivedError(view: WebView, request: WebResourceRequest, error: WebResourceError) {
            super.onReceivedError(view, request, error)
            helperMethods.dismissProgressDialog()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Log.d("errors : ", error.description.toString())
            }
        }
    }
}