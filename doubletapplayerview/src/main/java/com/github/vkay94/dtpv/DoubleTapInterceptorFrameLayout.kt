package com.github.vkay94.dtpv

import android.content.Context
import android.os.Handler
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.FrameLayout
import androidx.core.view.GestureDetectorCompat

/**
 * Custom player class for Double-Tapping listening
 */
open class DoubleTapInterceptorFrameLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    var blockTaps: Boolean = false
        set(value) {
            if (!value) {
                rippleView?.inDoubleTapMode(false)
            }
            field = value
        }

    var enableDoubleTaps: Boolean = true
        set(value) {
            blockTaps = false
            field = value
        }

    private val detector = GestureDetectorCompat(context, DoubleTapGestureListener())
    private var rippleView: DoubleTapRippleView? = null

    private var dtOccurred: Boolean = false

    fun rippleView(rippleView: DoubleTapRippleView) = apply {
        this.rippleView = rippleView
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        if (!enableDoubleTaps) {
            return false
        }
        detector.onTouchEvent(ev)
        return blockTaps && rippleView?.multiDoubleTap == true
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        rippleView?.inDoubleTapMode(true)
        rippleView?.onTouchEvent(event)

        return true
    }

    private inner class DoubleTapGestureListener : GestureDetector.SimpleOnGestureListener() {

        private val DELAY = 100L

        private val mHandler = Handler()
        private val mRunnable = Runnable {
            dtOccurred = false
        }

        override fun onDoubleTap(e: MotionEvent): Boolean {
            mHandler.removeCallbacks(mRunnable)
            mHandler.postDelayed(mRunnable, DELAY)
            dtOccurred = true
            blockTaps = true
            return super.onDoubleTap(e)
        }

        override fun onDoubleTapEvent(e: MotionEvent): Boolean {
            if (e.actionMasked == MotionEvent.ACTION_UP && dtOccurred) {
                if (rippleView?.multiDoubleTap == false) {
                    rippleView?.inDoubleTapMode(true)
                    rippleView?.onDoubleTapProgressUp(e.x, e.y)

                    dtOccurred = false
                    blockTaps = false
                }
            }
            return true
        }
    }
}