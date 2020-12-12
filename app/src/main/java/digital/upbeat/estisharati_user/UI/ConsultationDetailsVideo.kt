package digital.upbeat.estisharati_user.UI

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.*
import digital.upbeat.estisharati_user.Helper.HelperMethods
import digital.upbeat.estisharati_user.R
import kotlinx.android.synthetic.main.activity_consultation_details_video.*

class ConsultationDetailsVideo : AppCompatActivity() {
    lateinit var helperMethods: HelperMethods
    lateinit var simpleExoPlayer: SimpleExoPlayer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_consultation_details_video)
        initViews()
        clickEvents()
        setUpPlayer()
    }

    fun initViews() {
        helperMethods = HelperMethods(this@ConsultationDetailsVideo)
    }

    fun clickEvents() {
        nav_back.setOnClickListener { finish() }
    }

    fun setUpPlayer() {
        simpleExoPlayer = SimpleExoPlayer.Builder(this@ConsultationDetailsVideo).build()
        val uri = Uri.parse("https://assets.mixkit.co/videos/preview/mixkit-a-man-doing-jumping-tricks-at-the-beach-1222-large.mp4")
        val mediaItem: MediaItem = MediaItem.fromUri(uri)
        exoPlayer.setPlayer(simpleExoPlayer)
        simpleExoPlayer.setMediaItem(mediaItem)
        simpleExoPlayer.prepare()
        simpleExoPlayer.play()
    }

    override fun onStop() {
        simpleExoPlayer.release()
        super.onStop()
    }
}