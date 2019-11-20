package com.github.vkay94.dtpv.youtube

import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.github.vkay94.dtpv.*
import com.google.android.exoplayer2.Player
import kotlinx.android.synthetic.main.yt_overlay_circle.view.*


/**
 * Overlay for [DoubleTapPlayerView] to create a similar UI/UX experience like the official
 * YouTube Android app.
 *
 * In comparison to [YouTubeDoubleTap] this overlay has the typical YouTube scaling circle
 * animation and provides some configurations which can't be accomplished with the regular
 * Android Ripple (I didn't find any options in the documentation ...).
 *
 * Be aware that, since this kind of animation calls invalidate() very often, see CircleClapTapView
 * + ValueAnimator, there is more work on the GPU. Depending on the device (especially low end)
 * and ROM, some frame drops can occur. But for my devices, I get the same GPU Profiler picture
 * on the official YouTube app, too.
 */
class YouTubeOverlay(context: Context?, private val attrs: AttributeSet?) :
    ConstraintLayout(context, attrs), PlayerDoubleTapListener {

    constructor(context: Context?) : this(context, null) {
        // Hide overlay initially when added programmatically
        this.visibility = View.GONE
    }

    companion object {
        const val TAG = ".YouTubeOverlay"
        const val DEBUG = BuildConfig.BUILD_TYPE != "release"
    }

    private var playerViewRef: Int = -1

    // Animations
    private var forwardAnimation: AnimationDrawable
    private var rewindAnimation: AnimationDrawable

    // Player behaviors
    private var playerView: DoubleTapPlayerView? = null
    private var player: Player? = null
    private var currentRewindForward = 0

    init {
        LayoutInflater.from(context).inflate(R.layout.yt_overlay_circle, this, true)

        // Initialize UI components
        forwardAnimation = ContextCompat.getDrawable(
            context!!,
            R.drawable.yt_forward_animation
        ) as AnimationDrawable
        rewindAnimation = ContextCompat.getDrawable(
            context,
            R.drawable.yt_rewind_animation
        ) as AnimationDrawable

        textview_forward.setCompoundDrawablesWithIntrinsicBounds(null, forwardAnimation, null, null)
        textview_rewind.setCompoundDrawablesWithIntrinsicBounds(null, rewindAnimation, null, null)

        initializeAttributes()

        // This code snipped is executed when the circle scale animation is finished
        circle_clip_tap_view.performAtEnd = {
            performListener?.onEnd()

            rewind_container.visibility = View.INVISIBLE
            forward_container.visibility = View.INVISIBLE

            forwardAnimation.stop()
            rewindAnimation.stop()
        }
    }

    /**
     * Sets all optional XML attributes and defaults
     */
    private fun initializeAttributes() {
        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs,
                R.styleable.YouTubeOverlay, 0, 0)

            // PlayerView => see onAttackToWindow
            playerViewRef = a.getResourceId(R.styleable.YouTubeOverlay_yt_playerView, -1)

            // Durations
            circle_clip_tap_view.animationDuration = a.getInt(
                R.styleable.YouTubeOverlay_yt_animationDuration, 650).toLong()

            fastForwardRewindDuration = a.getInt(
                R.styleable.YouTubeOverlay_yt_ffrDuration, 10000)

            // Arc size
            circle_clip_tap_view.arcSize = a.getDimensionPixelSize(
                R.styleable.YouTubeOverlay_yt_arcSize,
                context.resources.getDimensionPixelSize(R.dimen.dtpv_yt_arc_size)).toFloat()

            // Colors
            circle_clip_tap_view.circleColor = a.getColor(
                R.styleable.YouTubeOverlay_yt_tapCircleColor,
                ContextCompat.getColor(context, R.color.dtpv_yt_tap_circle_color)
            )

            circle_clip_tap_view.circleBackgroundColor = a.getColor(
                R.styleable.YouTubeOverlay_yt_backgroundCircleColor,
                ContextCompat.getColor(context, R.color.dtpv_yt_background_circle_color)
            )

            a.recycle()

        } else {
            // Set defaults

            circle_clip_tap_view.animationDuration = 650

            circle_clip_tap_view.arcSize =
                context.resources.getDimensionPixelSize(R.dimen.dtpv_yt_arc_size).toFloat()

            circle_clip_tap_view.circleColor =
                ContextCompat.getColor(context, R.color.dtpv_yt_tap_circle_color)

            circle_clip_tap_view.circleBackgroundColor =
                ContextCompat.getColor(context, R.color.dtpv_yt_background_circle_color)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        // If the PlayerView is set by XML then call the corresponding setter method
        if (playerViewRef != -1)
            setupPlayer((this.parent as View).findViewById(playerViewRef) as DoubleTapPlayerView)
    }

    /**
     * Obligatory call if playerView is not set via XML!
     * Links the DoubleTapPlayerView to this view for performing seekTo-calls
     * and hiding the controller
     *
     * @param playerView PlayerView which triggers the event
     */
    fun setupPlayer(playerView: DoubleTapPlayerView) {
        this.playerView = playerView
        this.player = playerView.player
    }

    /*
        Properties
     */

    /**
     * Optional: Set a listener to observe whether double tap reached the start / end of the video
     */
    var seekListener: SeekListener? = null

    /**
     * Set a listener to execute some code before and after the animation
     * (for example UI changes (hide and show views etc.))
     */
    var performListener: PerformListener? = null

    /**
     * Forward / rewind duration on a tap
     */
    var fastForwardRewindDuration = 10000

    /**
     * Color of the scaling circle on touch feedback
     */
    var tapCircleColor: Int
        get() = circle_clip_tap_view.circleColor
        set(value) {
            circle_clip_tap_view.circleColor = value
        }

    /**
     * Color of the clipped background circle
     */
    var circleBackgroundColor: Int
        get() = circle_clip_tap_view.circleBackgroundColor
        set(value) {
            circle_clip_tap_view.circleBackgroundColor = value
        }

    /**
     * Duration of the circle scaling animation / speed.
     * The overlay keeps visible until the animation finishes.
     */
    var animationDuration: Long
        get() = circle_clip_tap_view.getCircleAnimator().duration
        set(value) {
            circle_clip_tap_view.getCircleAnimator().duration = value
        }

    /**
     * Size of the arc which will be clipped from the background circle.
     * The greater the value the more roundish the shape becomes
     */
    var arcSize: Float
        get() = circle_clip_tap_view.arcSize
        set(value) {
            circle_clip_tap_view.arcSize = value
        }

    /**
     * Sets the forward / rewind steps, default: 10 seconds
     */
    fun setForwardRewindIncrementMs(milliseconds: Int): YouTubeOverlay {
        this.fastForwardRewindDuration = milliseconds
        return this
    }

    // PlayerDoubleTapListener methods
    // YouTube behavior: the cases "started", "progressDown" and "finished" aren't handled
    //                   (the overlay does not disappear when the double tap mode is over,
    //                   ongoing taps simply do not trigger forward/rewind, the animation is finishing
    //                   separately (you can test it on the official YouTube app with setting
    //                   animation duration scaling to 5x in the developer options).
    //
    //                   It disappears when the circle scale animation is finished.
    override fun onDoubleTapProgressUp(posX: Float, posY: Float) {
        if (DEBUG) Log.d(TAG, "onDoubleTapProgressUp: $posX")

        // Check first whether forwarding/rewinding is "valid"
        if (player?.currentPosition == null || playerView?.width == null) return
        player?.currentPosition?.let { current ->
            // Rewind and start of the video (+ 0.5 sec tolerance)
            if (posX < playerView?.width!! * 0.35 && current <= 500)
                return

            // Forward and end of the video (- 0.5 sec tolerance)
            if (posX > playerView?.width!! * 0.65 && current >= player?.duration!!.minus(500))
                return
        }

        // YouTube behavior: show overlay on MOTION_UP
        // But check whether the first double tap is in invalid area
        if (this.visibility != View.VISIBLE) {
            if (posX < playerView?.width!! * 0.35 || posX > playerView?.width!! * 0.65)
                performListener?.onStart()
            else
                return
        }

        when {
            posX < playerView?.width!! * 0.35 -> {

                // First time tap or switched
                if (rewind_container.visibility != View.VISIBLE) {
                    currentRewindForward = 0

                    forward_container.visibility = View.INVISIBLE
                    rewind_container.visibility = View.VISIBLE
                    rewindAnimation.start()
                }

                // Cancel ripple and start new without triggering overlay disappearance
                // (resetting instead of ending)
                circle_clip_tap_view.resetAnimation {
                    circle_clip_tap_view.updatePosition(posX, posY)
                }
                rewinding()
            }
            posX > playerView?.width!! * 0.65 -> {

                // First time tap or switched
                if (forward_container.visibility != View.VISIBLE) {
                    currentRewindForward = 0

                    rewind_container.visibility = View.INVISIBLE
                    forward_container.visibility = View.VISIBLE
                    forwardAnimation.start()
                }

                // Cancel ripple and start new without triggering overlay disappearance
                // (resetting instead of ending)
                circle_clip_tap_view.resetAnimation {
                    circle_clip_tap_view.updatePosition(posX, posY)
                }
                forwarding()
            }
            else -> {
                // Invalid area tapped: cancel double tap mode and end circle scale animation
                // => "performAtEnd" is executed (see performAtEnd in the init block)
                playerView?.cancelInDoubleTapMode()
                circle_clip_tap_view.getCircleAnimator().end()
            }
        }
    }

    /**
     * Seeks the video to desired position.
     * Calls interface functions when start reached ([SeekListener.onVideoStartReached])
     * or when end reached ([SeekListener.onVideoEndReached])
     *
     * @param newPosition desired position
     */
    private fun seekToPosition(newPosition: Long) {
        if (DEBUG) Log.d(
            TAG,
            "seekToPosition: newPosition = $newPosition, duration = ${player?.duration}"
        )

        // Start of the video reached
        if (newPosition <= 0) {
            player?.seekTo(0)

            seekListener?.onVideoStartReached()
            return
        }

        // End of the video reached
        player?.duration?.let { total ->
            if (newPosition >= total) {
                player?.seekTo(total)

                seekListener?.onVideoEndReached()
                return
            }
        }

        // Otherwise
        playerView?.keepInDoubleTapMode()
        player?.seekTo(newPosition)
    }

    private fun forwarding() {
        currentRewindForward += fastForwardRewindDuration / 1000
        textview_forward.text =
            resources.getQuantityString(
                R.plurals.dtp_rf_seconds,
                currentRewindForward,
                currentRewindForward
            )

        seekToPosition(player?.currentPosition!!.plus(fastForwardRewindDuration))
    }

    private fun rewinding() {
        currentRewindForward += fastForwardRewindDuration / 1000
        textview_rewind.text =
            resources.getQuantityString(
                R.plurals.dtp_rf_seconds,
                currentRewindForward,
                currentRewindForward
            )

        seekToPosition(player?.currentPosition!!.minus(fastForwardRewindDuration))
    }

    interface PerformListener {
        /**
         * Called when the overlay is not visible and onDoubleTapProgressUp event occurred.
         * Visibility of the overlay should be set to VISIBLE within this interface method
         */
        fun onStart()

        /**
         * Called when the circle animation is finished.
         * Visibility of the overlay should be set to GONE within this interface method
         */
        fun onEnd()
    }
}
