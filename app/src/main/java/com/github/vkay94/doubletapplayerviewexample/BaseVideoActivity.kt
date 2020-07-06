package com.github.vkay94.doubletapplayerviewexample

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.github.vkay94.dtpv.DoubleTapPlayerView
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.LoadControl
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.activity_video.*


@SuppressLint("Registered")
open class BaseVideoActivity : AppCompatActivity() {

    var videoPlayer: DoubleTapPlayerView? = null
    var player: SimpleExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        supportActionBar?.hide()
    }

    fun buildMediaSource(mUri: Uri) {
        val bandwidthMeter = DefaultBandwidthMeter()
        val dataSourceFactory = DefaultDataSourceFactory(
            this@BaseVideoActivity,
            Util.getUserAgent(this@BaseVideoActivity, resources.getString(R.string.app_name)),
            bandwidthMeter
        )
        val videoSource = ProgressiveMediaSource.Factory(dataSourceFactory, Mp4ExtractorFactory())
            .createMediaSource(mUri)

        player?.prepare(videoSource)
        player?.playWhenReady = true
    }

    fun initializePlayer() {
        if (player == null) {
            val loadControl: LoadControl = DefaultLoadControl.Builder()
                .setBufferDurationsMs(
                    VideoPlayerConfig.MIN_BUFFER_DURATION,
                    VideoPlayerConfig.MAX_BUFFER_DURATION,
                    VideoPlayerConfig.MIN_PLAYBACK_START_BUFFER,
                    VideoPlayerConfig.MIN_PLAYBACK_RESUME_BUFFER
                )
                .createDefaultLoadControl()

            player = SimpleExoPlayer.Builder(this)
                .setLoadControl(loadControl)
                .build()

            playerView.player = player
        }
    }

    // Player Lifecycle
    fun releasePlayer() {
        if (player != null) {
            player?.release()
            player = null
        }
    }

    fun pausePlayer() {
        if (player != null) {
            player?.playWhenReady = false
            player?.playbackState
        }
    }

    fun resumePlayer() {
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

    companion object {
        fun <T: BaseVideoActivity> newIntent(context: Context, activity: Class<T>): Intent =
            Intent(context, activity)
    }
}
