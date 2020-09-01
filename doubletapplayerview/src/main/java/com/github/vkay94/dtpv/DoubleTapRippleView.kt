package com.github.vkay94.dtpv

import android.content.Context
import android.os.Handler
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.GestureDetectorCompat
import kotlinx.android.synthetic.main.yt_overlay.view.*


class DoubleTapRippleView(context: Context, private val attrs: AttributeSet?) :
    ConstraintLayout(context, attrs), PlayerDoubleTapListener {

    private val gestureListener: DoubleTapGestureListener = DoubleTapGestureListener(this).apply {
        controls = this@DoubleTapRippleView
    }
    private val gestureDetector = GestureDetectorCompat(context, gestureListener)

    constructor(context: Context) : this(context, null) {
        // Hide overlay initially when added programmatically
        this.visibility = View.INVISIBLE
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.yt_overlay, this, true)
        initializeAttributes()

        circle_clip_tap_view.performAtEnd = {
            performListener?.onAnimationEnd()
        }
    }

    private fun initializeAttributes() {
        if (attrs != null) {
            val a = context.obtainStyledAttributes(
                attrs,
                R.styleable.DoubleTapRippleView, 0, 0
            )

            animationDuration = a.getInt(
                R.styleable.DoubleTapRippleView_dtr_animationDuration, 650
            ).toLong()

            arcSize = a.getDimensionPixelSize(
                R.styleable.DoubleTapRippleView_dtr_arcSize,
                context.resources.getDimensionPixelSize(R.dimen.dtpv_yt_arc_size)
            ).toFloat()

            tapCircleColor = a.getColor(
                R.styleable.DoubleTapRippleView_dtr_tapCircleColor,
                ContextCompat.getColor(context, R.color.dtpv_yt_tap_circle_color)
            )

            circleBackgroundColor = a.getColor(
                R.styleable.DoubleTapRippleView_dtr_backgroundCircleColor,
                ContextCompat.getColor(context, R.color.dtpv_yt_background_circle_color)
            )

            a.recycle()

        } else {
            arcSize = context.resources.getDimensionPixelSize(R.dimen.dtpv_yt_arc_size).toFloat()
            tapCircleColor = ContextCompat.getColor(context, R.color.dtpv_yt_tap_circle_color)
            circleBackgroundColor =
                ContextCompat.getColor(context, R.color.dtpv_yt_background_circle_color)
            animationDuration = 650
        }
    }

    var multiDoubleTap = true
        private set

    /**
     * If `false` only *real* double taps are handled, if `true` ongoing taps are handled while
     * in double tap mode
     */
    fun multiDoubleTap(enabled: Boolean) = apply {
        multiDoubleTap = enabled
    }

    fun inDoubleTapMode(enabled: Boolean) = apply {
        gestureListener.isDoubleTapping = enabled
    }

    /**
     * Sets the duration how long an ongoing double tap is handled.
     */
    fun stayDelay(duration: Long) = apply {
        gestureListener.doubleTapDelay = duration
    }

    private var performListener: PerformListener? = null

    /**
     * Sets a listener to execute some code before and after the animation
     * (for example UI changes (hide and show views etc.))
     */
    fun performListener(listener: PerformListener) = apply {
        performListener = listener
    }

    private var clickListener: TapListener? = null

    /**
     * Sets a tap listener to observe single and double taps (UP)
     */
    fun tapListener(listener: TapListener) = apply {
        clickListener = listener
    }

    /**
     * Color of the scaling circle on touch feedback.
     */
    var tapCircleColor: Int
        get() = circle_clip_tap_view.circleColor
        private set(value) {
            circle_clip_tap_view.circleColor = value
        }

    fun tapCircleColorRes(@ColorRes resId: Int) = apply {
        tapCircleColor = ContextCompat.getColor(context, resId)
    }

    fun tapCircleColorInt(@ColorInt color: Int) = apply {
        tapCircleColor = color
    }

    /**
     * Color of the clipped background circle
     */
    var circleBackgroundColor: Int
        get() = circle_clip_tap_view.circleBackgroundColor
        private set(value) {
            circle_clip_tap_view.circleBackgroundColor = value
        }

    fun circleBackgroundColorRes(@ColorRes resId: Int) = apply {
        circleBackgroundColor = ContextCompat.getColor(context, resId)
    }

    fun circleBackgroundColorInt(@ColorInt color: Int) = apply {
        circleBackgroundColor = color
    }

    /**
     * Duration of the circle scaling animation / speed in milliseconds.
     * The overlay keeps visible until the animation finishes.
     */
    var animationDuration: Long
        get() = circle_clip_tap_view.animationDuration
        private set(value) {
            circle_clip_tap_view.animationDuration = value
        }

    fun animationDuration(duration: Long) = apply {
        animationDuration = duration
    }

    /**
     * Size of the arc which will be clipped from the background circle.
     * The greater the value the more roundish the shape becomes
     */
    var arcSize: Float
        get() = circle_clip_tap_view.arcSize
        internal set(value) {
            circle_clip_tap_view.arcSize = value
        }

    fun arcSize(@DimenRes resId: Int) = apply {
        arcSize = context.resources.getDimension(resId)
    }

    fun arcSize(px: Float) = apply {
        arcSize = px
    }

    override fun onDoubleTapProgressUp(posX: Float, posY: Float) {

        if (this.visibility != View.VISIBLE) {
            if (posX < width * 0.35 || posX > width * 0.65) {
                performListener?.onAnimationStart()
            } else
                return
        }

        when {
            posX < width * 0.35 -> {
                circle_clip_tap_view.resetAnimation {
                    circle_clip_tap_view.updatePosition(posX, posY)
                }
                clickListener?.onDoubleTap(LEFT)

                if (multiDoubleTap)
                    gestureListener.keepInDoubleTapMode()
                else
                    gestureListener.cancelInDoubleTapMode()
            }
            posX > width * 0.65 -> {
                circle_clip_tap_view.resetAnimation {
                    circle_clip_tap_view.updatePosition(posX, posY)
                }
                clickListener?.onDoubleTap(RIGHT)

                if (multiDoubleTap)
                    gestureListener.keepInDoubleTapMode()
                else
                    gestureListener.cancelInDoubleTapMode()
            }
            else -> {

            }
        }
    }

    interface PerformListener {
        /**
         * Called when the overlay is not visible and onDoubleTapProgressUp event occurred.
         * Visibility of the overlay should be set to VISIBLE within this interface method.
         */
        fun onAnimationStart()

        /**
         * Called when the circle animation is finished.
         * Visibility of the overlay should be set to GONE within this interface method.
         */
        fun onAnimationEnd()
    }

    interface TapListener {
        /**
         * Called when the circle animation is finished.
         * Visibility of the overlay should be set to GONE within this interface method.
         */
        fun onDoubleTap(gravity: Int)
    }

    inner class DoubleTapGestureListener(private val rootView: View) :
        GestureDetector.SimpleOnGestureListener() {

        private val mHandler = Handler()
        private val mRunnable = Runnable {
            if (DEBUG) Log.d(TAG, "Runnable called")
            isDoubleTapping = false
            controls?.onDoubleTapFinished()
        }

        var controls: PlayerDoubleTapListener? = null
        var isDoubleTapping = false
        var doubleTapDelay: Long = 250

        /**
         * Resets the timeout to keep in double tap mode.
         *
         * Called once in [PlayerDoubleTapListener.onDoubleTapStarted]. Needs to be called
         * from outside if the double tap is customized / overridden to detect ongoing taps
         */
        fun keepInDoubleTapMode() {
            isDoubleTapping = true
            mHandler.removeCallbacks(mRunnable)
            mHandler.postDelayed(mRunnable, doubleTapDelay)
        }

        /**
         * Cancels double tap mode instantly by calling [PlayerDoubleTapListener.onDoubleTapFinished]
         */
        fun cancelInDoubleTapMode() {
            mHandler.removeCallbacks(mRunnable)
            isDoubleTapping = false
            controls?.onDoubleTapFinished()
        }

        override fun onDown(e: MotionEvent): Boolean {
            // Used to override the other methods
            if (isDoubleTapping) {
                controls?.onDoubleTapProgressDown(e.x, e.y)
                return true
            }
            return super.onDown(e)
        }

        override fun onSingleTapUp(e: MotionEvent): Boolean {
            if (isDoubleTapping) {
                if (DEBUG) Log.d(TAG, "onSingleTapUp: isDoubleTapping = true")
                controls?.onDoubleTapProgressUp(e.x, e.y)
                return true
            }
            return super.onSingleTapUp(e)
        }

        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
            // Ignore this event if double tapping is still active
            // Return true needed because this method is also called if you tap e.g. three times
            // in a row, therefore the controller would appear since the original behavior is
            // to hide and show on single tap
            if (isDoubleTapping) return true
            if (DEBUG) Log.d(TAG, "onSingleTapConfirmed: isDoubleTap = false")

            return rootView.performClick()
        }

        override fun onDoubleTap(e: MotionEvent): Boolean {
            // First tap (ACTION_DOWN) of both taps
            if (DEBUG) Log.d(TAG, "onDoubleTap")
            if (!isDoubleTapping) {
                isDoubleTapping = true
                keepInDoubleTapMode()
                controls?.onDoubleTapStarted(e.x, e.y)
            }
            return true
        }

        override fun onDoubleTapEvent(e: MotionEvent): Boolean {
            // Second tap (ACTION_UP) of both taps
            if (e.actionMasked == MotionEvent.ACTION_UP && isDoubleTapping) {
                if (DEBUG) Log.d(
                    TAG,
                    "onDoubleTapEvent, ACTION_UP"
                )
                controls?.onDoubleTapProgressUp(e.x, e.y)
                return true
            }
            return super.onDoubleTapEvent(e)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        gestureDetector.onTouchEvent(event)
        return true
    }

    companion object {
        const val LEFT = 0
        const val RIGHT = 1

        private const val TAG = ".DTGListener"
        private var DEBUG = true
    }
}
