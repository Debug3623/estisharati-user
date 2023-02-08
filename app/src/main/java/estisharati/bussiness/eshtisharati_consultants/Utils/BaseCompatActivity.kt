package estisharati.bussiness.eshtisharati_consultants.Utils

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import estisharati.bussiness.eshtisharati_consultants.Helper.SharedPreferencesHelper

open class BaseCompatActivity : AppCompatActivity() {
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(InventumContextWrapper.wrap(newBase, SharedPreferencesHelper(newBase).appLang))
    }
}