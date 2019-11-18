package com.github.vkay94.doubletapplayerviewexample

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.github.vkay94.doubletapplayerviewexample.dialogs.ConfigDialogColors
import com.github.vkay94.doubletapplayerviewexample.dialogs.ConfigDialogVarious
import com.github.vkay94.dtpv.SeekListener
import com.github.vkay94.dtpv.youtube.YouTubeOverlay
import kotlinx.android.synthetic.main.activity_video_circle.*
import kotlinx.android.synthetic.main.exo_playback_control_view_circle.*


class VideoActivityCircle : BaseVideoActivity() {

    lateinit var youtubeDoubleTap: YouTubeOverlay

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_circle)

        this.videoPlayer = playerView
        initializePlayer()

        // Add DoubleTap behavior
        initializeDoubleTapPlayerView()
        initializeConfigButtons()

        btn_switch_mode.setOnClickListener {
            startActivity(newIntent(this@VideoActivityCircle, VideoActivityRipple::class.java))
            finish()
        }
    }

    private fun initializeDoubleTapPlayerView() {
//        youtubeDoubleTap
//            .setSeekListener(object : SeekListener {
//                override fun onVideoStartReached() {
//                    pausePlayer()
//                    Toast.makeText(this@VideoActivityCircle,
//                        "Video start reached", Toast.LENGTH_SHORT
//                    ).show()
//                }
//
//                override fun onVideoEndReached() {
//                    Toast.makeText(this@VideoActivityCircle,
//                        "Video end reached", Toast.LENGTH_SHORT
//                    ).show()
//                }
//            })

        // Setup with code
        youtubeDoubleTap = YouTubeOverlay(this).apply {
            setupPlayer(playerView)
            animationDuration = 800
            fastForwardRewindDuration = 10000
            seekListener = object : SeekListener {
                override fun onVideoStartReached() {
                    pausePlayer()
                    Toast.makeText(
                        this@VideoActivityCircle,
                        "Video start reached", Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onVideoEndReached() {
                    Toast.makeText(
                        this@VideoActivityCircle,
                        "Video end reached", Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        val layoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.MATCH_PARENT
        )

        youtubeDoubleTap.layoutParams = layoutParams
        cl_container_video_circle.addView(youtubeDoubleTap)

        playerView.activateDoubleTap(true)
            .setDoubleTapDelay(650)
            .setDoubleTapListener(youtubeDoubleTap)

        val videoUrl = DataAndUtils.videoList.first()
        buildMediaSource(Uri.parse(videoUrl))
    }

    private fun initializeConfigButtons() {
        btn_colors.setOnClickListener {
            ConfigDialogColors.newInstance(object : ConfigDialogColors.ColorChangedListener {
                override fun onTapColorChanged(newColor: Int) {
                    youtubeDoubleTap.tapCircleColor = newColor
                }

                override fun onBackgroundColorChanged(newColor: Int) {
                    youtubeDoubleTap.circleBackgroundColor = newColor
                }

            }, youtubeDoubleTap.tapCircleColor, youtubeDoubleTap.circleBackgroundColor)
                .show(supportFragmentManager, "COLORS DIALOG")
        }

        btn_various.setOnClickListener {
            val arcSizeInDp = DataAndUtils.pxToDp(this, youtubeDoubleTap.arcSize)

            ConfigDialogVarious.newInstance(object : ConfigDialogVarious.VariousChangedListener {
                override fun onDoubleTapDurationChanged(newDuration: Long) {
                    playerView.setDoubleTapDelay(newDuration.toInt())
                }

                override fun onYoutubeAnimationDurationChanged(newDuration: Long) {
                    youtubeDoubleTap.animationDuration = newDuration
                }

                override fun onArcSizeChanged(newDimen: Int) {
                    youtubeDoubleTap.arcSize =
                        DataAndUtils.dpToPx(this@VideoActivityCircle, newDimen.toFloat())
                }

            }, playerView.doubleTapDelay.toInt(), youtubeDoubleTap.animationDuration.toInt(),
                arcSizeInDp.toInt())
                .show(supportFragmentManager, "VARIOUS DIALOG")
        }
    }
}
