package com.github.vkay94.doubletapplayerviewexample.ripple

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.vkay94.doubletapplayerviewexample.R
import com.github.vkay94.dtpv.DoubleTapRippleView
import kotlinx.android.synthetic.main.activity_ripple_view.*

class RippleViewActivity : AppCompatActivity() {

    private var counter = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ripple_view)

        textView.setOnClickListener {
            Toast.makeText(this, "Clicked", Toast.LENGTH_SHORT).show()
        }

        textView.text = counter.toString()

        multiTapCheckBox.setOnCheckedChangeListener { buttonView, isChecked ->
//            interceptLayout.enableDoubleTaps = isChecked
            overlay.multiDoubleTap(isChecked)
            buttonView.isChecked = isChecked
        }

        multiTapCheckBox.isChecked = true

        interceptLayout.rippleView(overlay)
        interceptLayout.enableDoubleTaps = true
        overlay.stayDelay(0)
            .performListener(object : DoubleTapRippleView.PerformListener {
                override fun onAnimationStart() {
                    overlay.visibility = View.VISIBLE
                }

                override fun onAnimationEnd() {
                    overlay.visibility = View.INVISIBLE

                    interceptLayout.blockTaps = false
                    Toast.makeText(this@RippleViewActivity, "Released", Toast.LENGTH_SHORT).show()
                }
            })
            .tapListener(object : DoubleTapRippleView.TapListener {
                override fun onDoubleTap(gravity: Int) {

                    when (gravity) {
                        DoubleTapRippleView.LEFT -> counter -= 1
                        DoubleTapRippleView.RIGHT -> counter += 1
                    }

                    val text = when (gravity) {
                        DoubleTapRippleView.LEFT -> "onDoubleTap left"
                        DoubleTapRippleView.RIGHT -> "onDoubleTap right"
                        else -> "Clicked else"
                    }

                    lastActionTextView.text = "Last action: $text"
                    textView.text = counter.toString()
                }
            })
    }
}