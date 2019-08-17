package com.github.vkay94.dtpv

import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.google.android.exoplayer2.Player

class YouTubeDoubleTap(context: Context?, attrs: AttributeSet?) : ConstraintLayout(context, attrs),
    DoubleTapPlayerView.PlayerDoubleTapListener {

    val TAG = ".YouTubeDoubleTap"
    var DEBUG = BuildConfig.BUILD_TYPE != "release"

    // Layout-Views
    var tvForward: TextView
    var tvRewind: TextView
    var forwardContainer: FrameLayout
    var rewindContainer: FrameLayout

    // Animations
    var forwardAnimation: AnimationDrawable
    var rewindAnimation: AnimationDrawable

    // Player behaviors
    private var playerView: DoubleTapPlayerView? = null
    private var player: Player? = null

    var FAST_FORWARD_REWIND_SKIP = 10000

    // YouTube specific
    var currentRewindForward = 0

    init {
        LayoutInflater.from(context).inflate(R.layout.yt_overlay, this, true)
        hide()

        // Initialize UI components

        tvForward = findViewById(R.id.tvForward)
        tvRewind = findViewById(R.id.tvRewind)
        forwardContainer = findViewById(R.id.forwardFrameLayout)
        rewindContainer = findViewById(R.id.rewindFrameLayout)

        forwardAnimation =
            ContextCompat.getDrawable(context!!, R.drawable.yt_forward_animation) as AnimationDrawable
        rewindAnimation =
            ContextCompat.getDrawable(context, R.drawable.yt_rewind_animation) as AnimationDrawable

        tvForward.setCompoundDrawablesWithIntrinsicBounds(null, forwardAnimation, null, null)
        tvRewind.setCompoundDrawablesWithIntrinsicBounds(null, rewindAnimation, null, null)

        // Click listeners
        rewindContainer.setOnClickListener { view ->
            playerView?.keepInDoubleTapMode()
            currentRewindForward += FAST_FORWARD_REWIND_SKIP / 1000
            tvRewind.text =
                resources.getString(R.string.dtp_rf_seconds, currentRewindForward)

            player?.seekTo(player?.currentPosition!!.minus(FAST_FORWARD_REWIND_SKIP))
        }

        forwardContainer.setOnClickListener { view ->
            playerView?.keepInDoubleTapMode()
            currentRewindForward += FAST_FORWARD_REWIND_SKIP / 1000
            tvForward.text =
                resources.getString(R.string.dtp_rf_seconds, currentRewindForward)

            player?.seekTo(player?.currentPosition!!.plus(FAST_FORWARD_REWIND_SKIP))
        }
    }

    // Important!
    fun setPlayer(playerView: DoubleTapPlayerView): YouTubeDoubleTap {
        this.playerView = playerView
        this.player = playerView.player
        return this
    }

    fun setForwardRewindIncrementMs(milliseconds: Int): YouTubeDoubleTap {
        this.FAST_FORWARD_REWIND_SKIP = milliseconds
        return this
    }

    // PlayerDoubleTapListener methods

    override fun onDoubleTapStarted(posX: Float, posY: Float) {
        if (DEBUG) Log.d(TAG, "onDoubleTapStarted: $posX")

        // Hide controls (hideController() wouldn't work correctly if playWhenReady is false
        // (the canvas would flack))
//        playerView?.useController = false
//        show()
    }

    override fun onDoubleTapProgressUp(posX: Float, posY: Float) {
        if (DEBUG) Log.d(TAG, "onDoubleTapProgressUp: $posX")

        // YouTube behavior: show overlay on MOTION_UP:
        if (this.visibility == View.GONE) {
            playerView?.useController = false
            show()
        }

        // Method called when already in double tap mode and tapping area is different
        // (for example: started left and then right/middle)

        currentRewindForward = FAST_FORWARD_REWIND_SKIP / 1000

        val value = when {
            posX < width * 0.35 -> {
                if (forwardContainer.visibility == View.VISIBLE)
                    forwardContainer.visibility = View.INVISIBLE

                tvRewind.text =
                    resources.getString(R.string.dtp_rf_seconds, currentRewindForward)

                rewindContainer.visibility = View.VISIBLE
                rewindAnimation.start()

                -1
            }
            posX > width * 0.65 -> {

                if (rewindContainer.visibility == View.VISIBLE)
                    rewindContainer.visibility = View.INVISIBLE

                tvForward.text =
                    resources.getString(R.string.dtp_rf_seconds, currentRewindForward)

                forwardContainer.visibility = View.VISIBLE
                forwardAnimation.start()

                1
            }
            else -> {
                playerView?.cancelInDoubleTapMode()
                return
            }
        }

        val newPosition = player?.currentPosition?.plus(value * FAST_FORWARD_REWIND_SKIP)
        newPosition?.let { player?.seekTo(newPosition) }
    }

    override fun onDoubleTapFinished() {
        if (DEBUG) Log.d(TAG, "onDoubleTapFinished")

        // Hide overlay and re-add the controller to the player and show controls
        // if the player was paused previously
        playerView?.useController = true
        hide()

        if (!player?.playWhenReady!!) playerView?.showController()

        // Set overlay to its original state
        rewindContainer.visibility = View.INVISIBLE
        forwardContainer.visibility = View.INVISIBLE

        forwardAnimation.stop()
        rewindAnimation.stop()
    }

    override fun onDoubleTapProgressDown(posX: Float, posY: Float) {
        if (DEBUG) Log.d(TAG, "onDoubleTapProgressDown")
    }


    fun hide() {
        this.visibility = View.GONE
    }

    fun show() {
        this.visibility = View.VISIBLE
    }
}
