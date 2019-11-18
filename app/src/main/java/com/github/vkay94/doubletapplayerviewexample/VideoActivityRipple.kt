package com.github.vkay94.doubletapplayerviewexample

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import com.github.vkay94.dtpv.SeekListener
import kotlinx.android.synthetic.main.activity_video_ripple.*
import kotlinx.android.synthetic.main.exo_playback_control_view_ripple.*

class VideoActivityRipple : BaseVideoActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_ripple)

        this.videoPlayer = playerView
        initializePlayer()

        // Add DoubleTap behavior
        initializeDoubleTapPlayerView()

        btn_switch_mode.setOnClickListener {
            startActivity(newIntent(this@VideoActivityRipple, VideoActivityCircle::class.java))
            finish()
        }
    }

    private fun initializeDoubleTapPlayerView() {
        youtubeDoubleTap
            .setPlayer(playerView)
            .setForwardRewindIncrementMs(10000)
            .setSeekListener(object : SeekListener {
                override fun onVideoStartReached() {
                    pausePlayer()
                    Toast.makeText(this@VideoActivityRipple,
                        "Video start reached", Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onVideoEndReached() {
                    Toast.makeText(this@VideoActivityRipple,
                        "Video end reached", Toast.LENGTH_SHORT
                    ).show()
                }
            })

        playerView.activateDoubleTap(true)
            .setDoubleTapDelay(700)
            .setDoubleTapListener(youtubeDoubleTap)

        val videoUrl = DataAndUtils.videoList.first()
        buildMediaSource(Uri.parse(videoUrl))
    }
}
