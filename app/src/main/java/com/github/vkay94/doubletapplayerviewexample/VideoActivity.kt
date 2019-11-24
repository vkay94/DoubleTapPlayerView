package com.github.vkay94.doubletapplayerviewexample

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.github.vkay94.doubletapplayerviewexample.dialogs.ConfigDialogColors
import com.github.vkay94.doubletapplayerviewexample.dialogs.ConfigDialogVarious
import com.github.vkay94.dtpv.SeekListener
import com.github.vkay94.dtpv.youtube.YouTubeOverlay
import kotlinx.android.synthetic.main.activity_video.*
import kotlinx.android.synthetic.main.exo_playback_control_view_yt.*


class VideoActivity : BaseVideoActivity() {

    private var currentVideoId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)

        this.videoPlayer = playerView
        initializePlayer()

        // Add DoubleTap behavior
        initializeDoubleTapPlayerView()
        youtubeDoubleTap.setPlayer(player!!)

        initializeConfigButtons()

        btn_change_video.setOnClickListener {
            releasePlayer()
            initializePlayer()

            // Player has been released (set to null), so it has to be re-added to the overlay
            youtubeDoubleTap.setPlayer(player!!)

            currentVideoId = (currentVideoId + 1).rem(DataAndUtils.videoList.size)
            val videoUrl = DataAndUtils.videoList[currentVideoId]
            buildMediaSource(Uri.parse(videoUrl))
        }

        // Start first video at start
        val videoUrl = DataAndUtils.videoList[currentVideoId]
        buildMediaSource(Uri.parse(videoUrl))
    }

    private fun initializeDoubleTapPlayerView() {
        youtubeDoubleTap.apply {
            setPlayerView(playerView)
            animationDuration = 800
            fastForwardRewindDuration = 10000
            seekListener = object : SeekListener {
                override fun onVideoStartReached() {
                    pausePlayer()
                    Toast.makeText(
                        this@VideoActivity,
                        "Video start reached", Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onVideoEndReached() {
                    Toast.makeText(
                        this@VideoActivity,
                        "Video end reached", Toast.LENGTH_SHORT
                    ).show()
                }
            }
            performListener = object : YouTubeOverlay.PerformListener {
                override fun onAnimationStart() {
                    // Do UI changes when double tapping starts including showing the overlay
                    playerView?.useController = false
                    youtubeDoubleTap.visibility = View.VISIBLE
                }

                override fun onAnimationEnd() {
                    // Do UI changes when double tapping ends including hiding the overlay
                    youtubeDoubleTap.visibility = View.GONE
                    playerView?.useController = true

                    if (!player?.playWhenReady!!) playerView.showController()
                }
            }
        }

        playerView.activateDoubleTap(true)
            .setDoubleTapDelay(650)
            .setDoubleTapListener(youtubeDoubleTap)
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
                        DataAndUtils.dpToPx(this@VideoActivity, newDimen.toFloat())
                }

            }, playerView.doubleTapDelay.toInt(), youtubeDoubleTap.animationDuration.toInt(),
                arcSizeInDp.toInt())
                .show(supportFragmentManager, "VARIOUS DIALOG")
        }
    }
}
