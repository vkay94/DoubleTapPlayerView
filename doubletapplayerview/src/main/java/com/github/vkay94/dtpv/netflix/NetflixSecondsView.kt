package com.github.vkay94.dtpv.netflix

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.github.vkay94.dtpv.R
import me.ertugrul.lib.Forward
import me.ertugrul.lib.Rewind

/**
 * Layout group which handles the icon animation while forwarding and rewinding.
 *
 * Since it's based on view's alpha the fading effect is more fluid (more YouTube-like) than
 * using static drawables, especially when [cycleDuration] is low.
 *
 * Used by [YouTubeOverlay][com.github.vkay94.dtpv.youtube.YouTubeOverlay].
 */
class NetflixSecondsView(context: Context, attrs: AttributeSet?) :
    ConstraintLayout(context, attrs) {

    private var trianglesContainer: LinearLayout
    private var forwardIcon: Forward
    private var rewindIcon: Rewind

    init {
        LayoutInflater.from(context).inflate(R.layout.nf_seconds_view, this, true)

        trianglesContainer = findViewById(R.id.triangle_container)
        forwardIcon = findViewById(R.id.forward)
        rewindIcon = findViewById(R.id.rewind)
    }

    /**
     * Defines the duration for a full cycle of the triangle animation.
     * Each animation step takes 20% of it.
     */
    var cycleDuration: Long = 750L
        set(value) {
            field = value
        }

    /**
     * Sets the `TextView`'s seconds text according to the device`s language.
     */
    var seconds: Int = 0
        set(value) {
            forwardIcon.setSeekForwardInSeconds(value)
            rewindIcon.setSeekBackwardsInSeconds(value)
            field = value
        }

    /**
     * Mirrors the triangles depending on what kind of type should be used (forward/rewind).
     */
    var isForward: Boolean = true
        set(value) {
            field = value
        }


    /**
     * Starts the triangle animation
     */
    fun start() {
        stop()
        Log.wtf("Seconds view"," start is forward? $isForward")
        forwardIcon.isVisible = isForward
        rewindIcon.isVisible = !isForward
        if (isForward){
            forwardIcon.invokeAnimation()
        }else{
            rewindIcon.invokeAnimation()
        }
    }

    /**
     * Stops the triangle animation
     */
    fun stop() {
        reset()
    }

    private fun reset() {
    }

}