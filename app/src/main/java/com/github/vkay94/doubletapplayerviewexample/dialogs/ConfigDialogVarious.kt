package com.github.vkay94.doubletapplayerviewexample.dialogs

import android.os.Bundle
import android.view.*
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.fragment.app.DialogFragment
import com.github.vkay94.doubletapplayerviewexample.R
import kotlinx.android.synthetic.main.dialog_various.*

class ConfigDialogVarious : DialogFragment() {

    lateinit var listener: VariousChangedListener
    private val minDoubleTapDuration = 500
    private val minAnimationDuration = 500
    private val minArcSize = 0

    private val maxDoubleTapDuration = 2500
    private val maxAnimationDuration = 3000
    private val maxArcSize = 200

    val durationSteps = 50
    val arcSteps = 5

    var currentDoubleTapDuration: Int = 500
    var currentYouTubeAnimationDuration: Int = 650
    var currentArcSize = 50


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL,
            R.style.FullScreenPlayerSettingsDialogStyle
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_various, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.let {
            initializeDurationSeekBar(
                seekbar_double_tap_duration,
                tv_double_tap_duration,
                maxDoubleTapDuration,
                minDoubleTapDuration,
                currentDoubleTapDuration
            ) {
                listener.onDoubleTapDurationChanged(it.toLong())
            }

            initializeDurationSeekBar(
                seekbar_youtube_animation_duration,
                tv_youtube_animation_duration,
                maxAnimationDuration,
                minAnimationDuration,
                currentYouTubeAnimationDuration
            ) {
                listener.onYoutubeAnimationDurationChanged(it.toLong())
            }

            initializeDimensSeekBar(
                seekbar_arc_size,
                tv_arc_size,
                maxArcSize,
                minArcSize,
                currentArcSize
            ) {
                listener.onArcSizeChanged(it)
            }
        }
    }

    private fun initializeDurationSeekBar(seekBar: AppCompatSeekBar? = null, textView: TextView? = null,
                                          maxValue: Int = 0, minValue: Int = 0, currentValue: Int = 0,
                                          progressChanged: (progress: Int) -> Unit) {

        val textInitial = "$currentValue ms"
        textView?.text = textInitial

        seekBar?.apply {
            max = (maxValue - minValue) / durationSteps
            progress = (currentValue - minValue) / durationSteps
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    val value = minValue + (progress * durationSteps)
                    val text = "$value ms"
                    textView?.text = text

                    progressChanged(value)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    // Do nothing
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    // Do nothing
                }

            })
        }
    }

    private fun initializeDimensSeekBar(seekBar: AppCompatSeekBar? = null, textView: TextView? = null,
                                          maxValue: Int = 0, minValue: Int = 0, currentValue: Int = 0,
                                          progressChanged: (progress: Int) -> Unit) {

        val textInitial = "$currentValue dp"
        textView?.text = textInitial

        seekBar?.apply {
            max = (maxValue - minValue) / arcSteps
            progress = (currentValue - minValue) / arcSteps
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    val value = minValue + (progress * arcSteps)
                    val text = "$value dp"
                    textView?.text = text

                    progressChanged(value)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    // Do nothing
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    // Do nothing
                }

            })
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

    interface VariousChangedListener {
        fun onDoubleTapDurationChanged(newDuration: Long)
        fun onYoutubeAnimationDurationChanged(newDuration: Long)
        fun onArcSizeChanged(newDimen: Int)
    }

    companion object {

        val TAG = ConfigDialogVarious::class.java.simpleName

        fun newInstance(listener: VariousChangedListener,
                        dtDuration: Int, ytDuration: Int, arcSize: Int
        ): ConfigDialogVarious {
            return ConfigDialogVarious().apply {
                this.listener = listener
                this.currentDoubleTapDuration = dtDuration
                this.currentYouTubeAnimationDuration = ytDuration
                this.currentArcSize = arcSize
            }
        }
    }
}