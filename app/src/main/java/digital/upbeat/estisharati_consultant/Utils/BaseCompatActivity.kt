package digital.upbeat.estisharati_consultant.Utils

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import digital.upbeat.estisharati_consultant.Helper.SharedPreferencesHelper

open class BaseCompatActivity : AppCompatActivity() {
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(InventumContextWrapper.wrap(newBase, SharedPreferencesHelper(newBase).appLang))
    }
}