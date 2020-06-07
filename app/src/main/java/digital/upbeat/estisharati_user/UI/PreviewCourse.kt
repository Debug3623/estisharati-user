package digital.upbeat.estisharati_user.UI

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.viewpager.widget.ViewPager
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.material.tabs.TabLayout
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
        showRatingPopup()
    }

    fun initViews() {
        helperMethods = HelperMethods(this@PreviewCourse)
    }

    fun clickEvents() {
        nav_back.setOnClickListener { finish() }
    }

    fun setUpPlayer() {
        val bandwidthMeter = DefaultBandwidthMeter()
        val trackSelector = DefaultTrackSelector(AdaptiveTrackSelection.Factory(bandwidthMeter))
        simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(this@PreviewCourse, trackSelector)
        val uri = Uri.parse("https://www.radiantmediaplayer.com/media/bbb-360p.mp4")
        //        val uri = Uri.parse("https://assets.mixkit.co/videos/preview/mixkit-a-man-doing-jumping-tricks-at-the-beach-1222-large.mp4")
        val defaultHttpDataSource = DefaultHttpDataSourceFactory("player")
        val extractorFactory = DefaultExtractorsFactory()
        val mediaSource = ExtractorMediaSource.Factory(defaultHttpDataSource).setExtractorsFactory(extractorFactory).createMediaSource(uri)

        player.player = simpleExoPlayer
        simpleExoPlayer.prepare(mediaSource)
        simpleExoPlayer.playWhenReady = true
        simpleExoPlayer.addListener(object : Player.EventListener {
            override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {
            }

            override fun onSeekProcessed() {
            }

            override fun onTracksChanged(trackGroups: TrackGroupArray?, trackSelections: TrackSelectionArray?) {
            }

            override fun onPlayerError(error: ExoPlaybackException?) {
                Log.d("Player", "" + error?.printStackTrace())
            }

            override fun onLoadingChanged(isLoading: Boolean) {
            }

            override fun onPositionDiscontinuity(reason: Int) {
            }

            override fun onRepeatModeChanged(repeatMode: Int) {
            }

            override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
            }

            override fun onTimelineChanged(timeline: Timeline?, manifest: Any?, reason: Int) {
            }

            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                when (playbackState) {
                    Player.STATE_IDLE -> {
                        Log.d("Player", "STATE_IDLE")
                    }
                    Player.STATE_BUFFERING -> {
                        Log.d("Player", "STATE_BUFFERING")
                    }
                    Player.STATE_READY -> {
                        Log.d("Player", "STATE_READY")
                    }
                    Player.STATE_ENDED -> {
                        Log.d("Player", "STATE_ENDED")
                    }
                }
            }
        })
    }

    fun tapInitialize() {
        val tabOne = layoutInflater.inflate(R.layout.tap_item, null) as View
        val tabTwo = layoutInflater.inflate(R.layout.tap_item, null) as View
        tabOne.tap_text.text = "Course content"
        tabTwo.tap_text.text = "Documents"
        setUpViewPager(course_viewpager)
        course_tablayout.setupWithViewPager(course_viewpager)
        course_tablayout.getTabAt(0)?.setCustomView(tabOne)
        course_tablayout.getTabAt(1)?.setCustomView(tabTwo)
        //        val linearLayout = search_tabLayout.getChildAt(0) as LinearLayout
        //        linearLayout.showDividers = LinearLayout.SHOW_DIVIDER_MIDDLE
        //        val drawable = GradientDrawable()
        //        drawable.setColor(resources.getColor(R.color.gainsboro_gray))
        //        drawable.setSize(2, 1)
        //        linearLayout.setDividerPadding(20);
        //        linearLayout.dividerDrawable = drawable
        //        tabOne.setBackgroundColor(getResources().getColor(R.color.lighter_white));
        //        search_tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
        //            @Override
        //            public void onTabSelected(TabLayout.Tab tab) {
        //                if (tab.getPosition() == 0) {
        //                    tabOne.setBackgroundColor(getResources().getColor(R.color.lighter_white));
        //                } else {
        //                    tabTwo.setBackgroundColor(getResources().getColor(R.color.lighter_white));
        //                }
        //
        //            }
        //
        //            @Override
        //            public void onTabUnselected(TabLayout.Tab tab) {
        //
        //            }
        //
        //            @Override
        //            public void onTabReselected(TabLayout.Tab tab) {
        //
        //            }
        //        });
        //        course_tablayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
        //            override fun onTabReselected(tab: TabLayout.Tab?) {
        //            }
        //
        //            override fun onTabUnselected(tab: TabLayout.Tab?) {
        //            }
        //
        //            override fun onTabSelected(tab: TabLayout.Tab?) {
        //
        //            }
        //        })
    }

    fun showRatingPopup() {
        val LayoutView = LayoutInflater.from(this@PreviewCourse).inflate(R.layout.rating_popup, null)
        val aleatdialog = android.app.AlertDialog.Builder(this@PreviewCourse)
        aleatdialog.setView(LayoutView)
        aleatdialog.setCancelable(false)
        val dialog = aleatdialog.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        val rating_bar = LayoutView.findViewById(R.id.rating_bar) as RatingBar
        val rating_based_cmd = LayoutView.findViewById(R.id.rating_based_cmd) as TextView
        val comments = LayoutView.findViewById(R.id.comments) as EditText
        val send = LayoutView.findViewById(R.id.send) as Button
        setRatingBasedCommend(rating_based_cmd, rating_bar.rating)

        rating_bar.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
            setRatingBasedCommend(rating_based_cmd, rating)
        }
        send.setOnClickListener {
            if (comments.text.toString().equals("")) {
                Toast.makeText(this@PreviewCourse, "Please feel free to leave your comments", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            dialog.dismiss()
        }
    }

    fun setRatingBasedCommend(rating_based_cmd: TextView, rating: Float) {
        when (Math.round(rating)) {
            0 -> {
                rating_based_cmd.text = "Very bad"
            }
            1 -> {
                rating_based_cmd.text = "Bad"
            }
            2 -> {
                rating_based_cmd.text = "Average"
            }
            3 -> {
                rating_based_cmd.text = "Good"
            }
            4 -> {
                rating_based_cmd.text = "Very good"
            }
            5 -> {
                rating_based_cmd.text = "Very impressive"
            }
        }
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