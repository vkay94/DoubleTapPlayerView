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


@SuppressLint("Registered")
open class BaseVideoActivity : AppCompatActivity() {

    var videoPlayer: DoubleTapPlayerView? = null
    var player: SimpleExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
    }

    fun buildMediaSource(mUri: Uri) {
        val dataSourceFactory = DefaultDataSourceFactory(
            this@BaseVideoActivity,
            Util.getUserAgent(this@BaseVideoActivity, resources.getString(R.string.app_name)),
            DefaultBandwidthMeter.Builder(this@BaseVideoActivity).build()
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
                    MIN_BUFFER_DURATION,
                    MAX_BUFFER_DURATION,
                    MIN_PLAYBACK_START_BUFFER,
                    MIN_PLAYBACK_RESUME_BUFFER
                )
                .createDefaultLoadControl()

            player = SimpleExoPlayer.Builder(this)
                .setLoadControl(loadControl)
                .build()

            videoPlayer?.player = player
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

    fun setFullscreen(fullscreen: Boolean) {
        if (fullscreen) {
            this.window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        } else {
            this.window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
    }

    companion object {
        const val MIN_BUFFER_DURATION = 15000
        const val MAX_BUFFER_DURATION = 60000
        const val MIN_PLAYBACK_START_BUFFER = 2500
        const val MIN_PLAYBACK_RESUME_BUFFER = 5000

        fun <T: BaseVideoActivity> newIntent(context: Context, activity: Class<T>): Intent =
            Intent(context, activity)
    }
}
