package com.github.vkay94.doubletapplayerviewexample.dialogs

import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import com.github.vkay94.doubletapplayerviewexample.R
import kotlinx.android.synthetic.main.dialog_colors.*

class ConfigDialogColors : DialogFragment() {

    lateinit var listener: ColorChangedListener
    var tapColor: Int = -1
    var backgroundColor: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL,
            R.style.FullScreenPlayerSettingsDialogStyle
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_colors, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.let {
            // Tap circle color
            v_tap_alpha_slider.setColor(tapColor)

            tap_color_picker_view.setInitialColor(tapColor, false)
            tap_color_picker_view.addOnColorChangedListener {selectedColor ->
                listener.onTapColorChanged(selectedColor)
            }

            // Background circle color
            v_background_alpha_slider.setColor(backgroundColor)

            background_color_picker_view.setInitialColor(backgroundColor, false)
            background_color_picker_view.addOnColorChangedListener {selectedColor ->
                listener.onBackgroundColorChanged(selectedColor)
            }
        }
    }

    override fun onStart() {
        super.onStart()

        val dialog = dialog
        if (dialog != null) {

            dialog.window?.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            dialog.window?.setDimAmount(0.50f)

            val metrics = context!!.resources.displayMetrics
            val widthRatio =
                resources.getInteger(R.integer.playersettings_dialog_width_ratio).toDouble() / 100
            val width = (metrics.widthPixels * widthRatio).toInt()

            // W, H
            dialog.window?.setLayout(width, ViewGroup.LayoutParams.MATCH_PARENT)
            dialog.window?.setGravity(Gravity.END)
        }
    }

    interface ColorChangedListener {
        fun onTapColorChanged(newColor: Int)
        fun onBackgroundColorChanged(newColor: Int)
    }

    companion object {

        val TAG = ConfigDialogColors::class.java.simpleName

        fun newInstance(listener: ColorChangedListener, tapColor: Int, backgroundColor: Int): ConfigDialogColors {
            return ConfigDialogColors().apply {
                this.listener = listener
                this.tapColor = tapColor
                this.backgroundColor = backgroundColor
            }
        }
    }
}