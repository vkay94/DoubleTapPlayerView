package com.github.vkay94.doubletapplayerviewexample.fragments

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.github.vkay94.doubletapplayerviewexample.R
import com.github.vkay94.doubletapplayerviewexample.TextViewStyler
import com.github.vkay94.doubletapplayerviewexample.databinding.FragmentSecondsViewBinding


class SecondsViewFragment : Fragment() {

    private var _binding: FragmentSecondsViewBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: PageViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSecondsViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.secondsView.seconds = 10
        binding.secondsView.textView.setTextColor(Color.WHITE)

        binding.secondsView.start()

        activity?.let { fa ->
            viewModel = ViewModelProvider(fa).get(PageViewModel::class.java)

            binding.textsizePicker.apply {
                minValue = 10
                maxValue = 20
                value = 13
                setFormatter { "$it sp" }
                setOnValueChangedListener { picker, _, _ ->
                    binding.secondsView.textView.textSize = picker.value.toFloat()
                    viewModel.fontSize.value = picker.value.toFloat()
                }
            }

            binding.typefacePicker.apply {
                minValue = 0
                maxValue = 3
                value = 0
                setFormatter {
                    when (it) {
                        0 -> "normal"
                        1 -> "bold"
                        2 -> "italic"
                        3 -> "bold italic"
                        else -> "undefined"
                    }
                }
                setOnValueChangedListener { picker, _, _ ->
                    val tf = when (picker.value) {
                        0 -> Typeface.NORMAL
                        1 -> Typeface.BOLD
                        2 -> Typeface.ITALIC
                        3 -> Typeface.BOLD_ITALIC
                        else -> 0
                    }

                    TextViewStyler()
                        .textStyle(tf).applyTo(binding.secondsView.textView)
                    viewModel.typeFace.value = tf
                }
            }

            binding.secondsPicker.apply {
                minValue = 0
                maxValue = 3
                value = 0
                setFormatter {
                    when (it) {
                        0 -> "default"
                        1 -> "24dp"
                        2 -> "smaller"
                        3 -> "No space"
                        else -> "undefined"
                    }
                }
                setOnValueChangedListener { picker, _, _ ->
                    val res = when (picker.value) {
                        0 -> R.drawable.seconds_icon_default
                        1 -> R.drawable.seconds_icon_24dp
                        2 -> R.drawable.seconds_icon_smaller
                        3 -> R.drawable.seconds_icon_no_space
                        else -> 0
                    }

                    if (res > 0) {
                        binding.secondsView.icon = res
                        viewModel.secondsIcon.value = res
                    }
                }
            }
        }

        initTriangleSpeedSeekbar(
            binding.seekbarYoutubeDrawableAnimationDuration,
            binding.tvYoutubeDrawableAnimationDuration,
            3000,
            500,
            binding.secondsView.cycleDuration.toInt()
        ) {
            binding.secondsView.cycleDuration = it.toLong()
            viewModel.iconSpeed.value = it.toLong()
        }
    }

    private fun initTriangleSpeedSeekbar(
        seekBar: AppCompatSeekBar? = null, textView: TextView? = null,
        maxValue: Int = 0, minValue: Int = 0, currentValue: Int = 0,
        progressChanged: (progress: Int) -> Unit
    ) {
        val textInitial = "$currentValue ms"
        textView?.text = textInitial

        seekBar?.apply {
            max = (maxValue - minValue) / 50
            progress = (currentValue - minValue) / 50
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    val value = minValue + (progress * 50)
                    val text = "$value ms"
                    textView?.text = text

                    progressChanged(value)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
                override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit

            })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(): SecondsViewFragment {
            return SecondsViewFragment()
        }
    }
}