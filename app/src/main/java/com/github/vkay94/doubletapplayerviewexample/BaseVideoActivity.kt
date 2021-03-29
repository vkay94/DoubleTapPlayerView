package com.github.vkay94.doubletapplayerviewexample

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.github.vkay94.dtpv.DoubleTapPlayerView
import com.google.android.exoplayer2.*
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
            .createMediaSource(MediaItem.fromUri(mUri))

        player?.apply {
            setMediaSource(videoSource)
            prepare()
        }
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
                .build()

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

    override fun onPause() {
        super.onPause()
        player?.pause()
    }

    override fun onRestart() {
        super.onRestart()
        if (player?.playbackState == Player.STATE_READY && player?.playWhenReady!!)
            player?.play()
    }

    override fun onDestroy() {
        super.onDestroy()
        releasePlayer()
    }

    fun setFullscreen(fullscreen: Boolean) {
        if (fullscreen) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN
                        and View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        and View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
            } else {
                window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN
                        and View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
            }
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
