package com.github.vkay94.doubletapplayerviewexample

import android.widget.TextView

class TextViewStyler {
    private var textSize: Float? = null
    private var textColor: Int? = null
    private var textStyle: Int? = null

    fun textSize(value: Float) = apply { textSize = value }
    fun textColor(value: Int) = apply { textColor = value }
    fun textStyle(value: Int) = apply { textStyle = value }

    fun applyTo(textView: TextView) {
        textSize?.let { textView.textSize = it }
        textColor?.let { textView.setTextColor(it) }
        textStyle?.let { textView.setTypeface(null, it) }
    }
}
