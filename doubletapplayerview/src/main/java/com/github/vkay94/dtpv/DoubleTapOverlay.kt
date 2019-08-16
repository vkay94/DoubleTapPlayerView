package com.github.vkay94.dtpv;

import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat

class DoubleTapOverlay(context: Context?, attrs: AttributeSet?) : ConstraintLayout(context, attrs) {

    var tvForward: TextView
    var tvRewind: TextView
    var forwardContainer: FrameLayout
    var rewindContainer: FrameLayout

    var forwardAnimation: AnimationDrawable
    var rewindAnimation: AnimationDrawable

    init {
        LayoutInflater.from(context).inflate(R.layout.double_tap_overlay, this, true)
        hide()

        tvForward = findViewById(R.id.tvForward)
        tvRewind = findViewById(R.id.tvRewind)
        forwardContainer = findViewById(R.id.forwardFrameLayout)
        rewindContainer = findViewById(R.id.rewindFrameLayout)

        forwardAnimation = ContextCompat.getDrawable(context!!, R.drawable.forward_animation) as AnimationDrawable
        rewindAnimation = ContextCompat.getDrawable(context, R.drawable.rewind_animation) as AnimationDrawable

        tvForward.setCompoundDrawablesWithIntrinsicBounds(null, forwardAnimation, null, null)
        tvRewind.setCompoundDrawablesWithIntrinsicBounds(null, rewindAnimation, null, null)
    }

    fun hide() {
        this.visibility = View.GONE
    }

    fun show() {
        this.visibility = View.VISIBLE
    }
}
