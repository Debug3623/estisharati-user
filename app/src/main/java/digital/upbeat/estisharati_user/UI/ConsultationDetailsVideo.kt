package digital.upbeat.estisharati_user.UI

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.Player.DefaultEventListener
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
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
        val bandwidthMeter = DefaultBandwidthMeter()
        val trackSelector = DefaultTrackSelector(AdaptiveTrackSelection.Factory(bandwidthMeter))
        simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(this@ConsultationDetailsVideo, trackSelector)
//        val uri = Uri.parse("https://tqsema.ae/tqsema.mp4")
                val uri = Uri.parse("https://assets.mixkit.co/videos/preview/mixkit-a-man-doing-jumping-tricks-at-the-beach-1222-large.mp4")
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
        //        player.player.addListener(object : DefaultEventListener() {
        //            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        //                if (playWhenReady && playbackState == Player.STATE_READY) {
        //                    // media actually playing
        //                } else if (playWhenReady) {
        //                    // might be idle (plays after prepare()),
        //                    // buffering (plays when data available)
        //                    // or ended (plays when seek away from end)
        //                } else {
        //                    // player paused in any state
        //                }
        //            }
        //        })
    }

    override fun onStop() {
        simpleExoPlayer.release()
        super.onStop()
    }
}