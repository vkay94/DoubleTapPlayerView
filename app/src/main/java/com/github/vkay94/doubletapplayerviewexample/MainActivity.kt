package com.github.vkay94.doubletapplayerviewexample

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.vkay94.dtpv.PlayerDoubleTapListener
import com.github.vkay94.dtpv.SeekListener
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelection
import com.google.android.exoplayer2.upstream.BandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), PlayerDoubleTapListener {

    private val TAG = ".MainActivity"
    private var player: SimpleExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        initializePlayer()

        // Add DoubleTap behavior
        doubleTapOverlay
            .setPlayer(playerView)
            .setForwardRewindIncrementMs(10000)
            .setSeekListener(object : SeekListener {
                override fun onVideoStartReached() {
                    player?.stop()
                    Toast.makeText(this@MainActivity,
                        "Video start reached", Toast.LENGTH_SHORT).show()
                }

                override fun onVideoEndReached() {
                    Toast.makeText(this@MainActivity,
                        "Video end reached", Toast.LENGTH_SHORT).show()
                }
            })

        playerView.activateDoubleTap(true)
            .setDoubleTapListener(doubleTapOverlay)
//            .setDoubleTapListener(this)
            .setDoubleTapDelay(500)

        // Start video
        // Found at: https://sample-videos.com/

        val videoUrl = "https://file-examples.com/wp-content/uploads/2017/04/file_example_MP4_1920_18MG.mp4"
        buildMediaSource(Uri.parse(videoUrl))
    }

    private fun initializePlayer() {
        if (player == null) {
            val loadControl: LoadControl = DefaultLoadControl.Builder()
                .setBufferDurationsMs(
                    VideoPlayerConfig.MIN_BUFFER_DURATION,
                    VideoPlayerConfig.MAX_BUFFER_DURATION,
                    VideoPlayerConfig.MIN_PLAYBACK_START_BUFFER,
                    VideoPlayerConfig.MIN_PLAYBACK_RESUME_BUFFER
                )
                .createDefaultLoadControl()

            val bandwidthMeter: BandwidthMeter = DefaultBandwidthMeter.Builder(this).build()
            val defaultRenderersFactory = DefaultRenderersFactory(this)

            val videoTrackSelectionFactory: TrackSelection.Factory =
                AdaptiveTrackSelection.Factory(bandwidthMeter)
            val trackSelector = DefaultTrackSelector(videoTrackSelectionFactory)

            player = ExoPlayerFactory.newSimpleInstance(
                this,
                defaultRenderersFactory,
                trackSelector,
                loadControl
            )
            playerView.player = player
        }
    }

    private fun buildMediaSource(mUri: Uri) {
        val bandwidthMeter = DefaultBandwidthMeter()
        val dataSourceFactory = DefaultDataSourceFactory(
            this@MainActivity,
            Util.getUserAgent(this@MainActivity, resources.getString(R.string.app_name)),
            bandwidthMeter
        )
        val videoSource = ExtractorMediaSource.Factory(dataSourceFactory)
            .createMediaSource(mUri)

        player?.prepare(videoSource)
        player?.playWhenReady = true
    }

    // Player Lifecycle
    private fun releasePlayer() {
        if (player != null) {
            player?.release()
            player = null
        }
    }

    private fun pausePlayer() {
        if (player != null) {
            player?.playWhenReady = false
            player?.playbackState
        }
    }

    private fun resumePlayer() {
        if (player != null) {
            player?.playWhenReady = true
            player?.playbackState
        }
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onRestart() {
        super.onRestart()
        if (player?.playbackState == Player.STATE_READY && player?.playWhenReady!!)
            resumePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()

        if (player != null) {
            player?.release()
            player = null
        }
    }

    // For debugging purposes

    override fun onDoubleTapStarted(posX: Float, posY: Float) {
        Log.d(TAG, "onDoubleTapStarted")
    }

    override fun onDoubleTapProgressDown(posX: Float, posY: Float) {
        Log.d(TAG, "onDoubleTapProgressDown")
    }

    override fun onDoubleTapProgressUp(posX: Float, posY: Float) {
        Log.d(TAG, "onDoubleTapProgressUp")
    }

    override fun onDoubleTapFinished() {
        Log.d(TAG, "onDoubleTapFinished")
    }
}
