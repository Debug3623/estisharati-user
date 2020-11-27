package digital.upbeat.estisharati_user.Utils

import android.content.Context
import androidx.appcompat.app.AppCompatActivity

open class BaseCompatActivity :AppCompatActivity(){
    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(InventumContextWrapper.wrap(newBase,"ar"))
    }
}