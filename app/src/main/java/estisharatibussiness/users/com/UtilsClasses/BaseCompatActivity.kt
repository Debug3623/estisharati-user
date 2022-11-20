package estisharatibussiness.users.com.UtilsClasses

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import estisharatibussiness.users.com.Helper.SharedPreferencesHelper

open class BaseCompatActivity : AppCompatActivity() {
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(InventumContextWrapper.wrap(newBase, SharedPreferencesHelper(newBase).appLang))
    }
}