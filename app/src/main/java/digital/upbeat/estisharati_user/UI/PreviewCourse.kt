package digital.upbeat.estisharati_user.UI

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.SimpleExoPlayer
import digital.upbeat.estisharati_user.Adapter.TapViewPagerAdapter
import digital.upbeat.estisharati_user.Fragment.*
import digital.upbeat.estisharati_user.Helper.HelperMethods
import digital.upbeat.estisharati_user.R
import kotlinx.android.synthetic.main.activity_preview_course.*
import kotlinx.android.synthetic.main.tap_item.view.*

class PreviewCourse : AppCompatActivity() {
    lateinit var helperMethods: HelperMethods
    lateinit var simpleExoPlayer: SimpleExoPlayer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview_course)
        initViews()
        clickEvents()
        setUpPlayer()
        tapInitialize()
    }

    fun initViews() {
        helperMethods = HelperMethods(this@PreviewCourse)
        simpleExoPlayer = SimpleExoPlayer.Builder(this@PreviewCourse).build()

    }

    fun clickEvents() {
        nav_back.setOnClickListener { finish() }
    }

    fun setUpPlayer() {
        val uri = Uri.parse("https://www.radiantmediaplayer.com/media/big-buck-bunny-360p.mp4")
        val mediaItem: MediaItem = MediaItem.fromUri(uri)
        exoPlayer.setPlayer(simpleExoPlayer)
        simpleExoPlayer.setMediaItem(mediaItem)
        simpleExoPlayer.prepare()
        simpleExoPlayer.play()

    }

    fun tapInitialize() {
        val tabOne = layoutInflater.inflate(R.layout.tap_item, null) as View
        val tabTwo = layoutInflater.inflate(R.layout.tap_item, null) as View
        tabOne.tap_text.text = "Course content"
        tabTwo.tap_text.text = "Documents"
        setUpViewPager(courseViewpager)
        courseTablayout.setupWithViewPager(courseViewpager)
        courseTablayout.getTabAt(0)?.setCustomView(tabOne)
        courseTablayout.getTabAt(1)?.setCustomView(tabTwo)

    }


    private fun setUpViewPager(viewPager: ViewPager) {
        val adapter = TapViewPagerAdapter(supportFragmentManager)
        adapter.addFrag(PreviewCourseContent(), "ONE")
        adapter.addFrag(Documents(), "TWO")
        viewPager.setAdapter(adapter)
    }

    override fun onStop() {
        simpleExoPlayer.release()
        super.onStop()
    }
}