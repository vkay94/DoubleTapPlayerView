package com.github.vkay94.doubletapplayerviewexample

import android.content.pm.ActivityInfo
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import com.github.vkay94.doubletapplayerviewexample.databinding.ActivityMainBinding
import com.github.vkay94.doubletapplayerviewexample.databinding.ExoPlaybackControlViewYtBinding
import com.github.vkay94.doubletapplayerviewexample.fragments.PageViewModel
import com.github.vkay94.doubletapplayerviewexample.fragments.SecondsViewFragment
import com.github.vkay94.doubletapplayerviewexample.fragments.SectionsPagerAdapter
import com.github.vkay94.doubletapplayerviewexample.fragments.ShapeFragment
import com.github.vkay94.dtpv.youtube.YouTubeOverlay

class MainActivity : BaseVideoActivity() {

    private var isVideoFullscreen = false
    private var currentVideoId = -1

    // View bindings
    private lateinit var binding: ActivityMainBinding
    private lateinit var controlsBinding: ExoPlaybackControlViewYtBinding

    private lateinit var viewModel: PageViewModel

    // Views (to eliminate repeating 'binding.'-prefixes)
    private lateinit var ytOverlay: YouTubeOverlay

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Setup layout views with View binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        val controls = view.findViewById<ConstraintLayout>(R.id.exo_controls_root)
        controlsBinding = ExoPlaybackControlViewYtBinding.bind(controls)
        setContentView(view)

        ytOverlay = binding.ytOverlay
        videoPlayer = binding.previewPlayerView

        setSupportActionBar(binding.toolbar)

        initDoubleTapPlayerView()
        initViewModel()
        startNextVideo()

        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        sectionsPagerAdapter.addFragment(ShapeFragment.newInstance())
        sectionsPagerAdapter.addFragment(SecondsViewFragment.newInstance())

        binding.viewPager.adapter = sectionsPagerAdapter
        binding.tabs.setupWithViewPager(binding.viewPager)

        controlsBinding.fullscreenButton.setOnClickListener {
            toggleFullscreen()
        }
    }

    private fun initDoubleTapPlayerView() {
        ytOverlay
            // Uncomment this line if the DoubleTapPlayerView is not set via XML
            //.playerView(previewPlayerView)
            .performListener(object : YouTubeOverlay.PerformListener {
                override fun onAnimationStart() {
                    binding.previewPlayerView.useController = false
                    ytOverlay.visibility = View.VISIBLE
                }
                override fun onAnimationEnd() {
                    ytOverlay.visibility = View.GONE
                    binding.previewPlayerView.useController = true
                }
            })

        binding.previewPlayerView.doubleTapDelay = 800
        // Uncomment this line if the PlayerDoubleTapListener is not set via XML
        // previewPlayerView.controller(ytOverlay)
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this).get(PageViewModel::class.java).apply {
            tapCircleColor.value = ytOverlay.tapCircleColor
            arcSize.value = DataAndUtils.pxToDp(this@MainActivity, ytOverlay.arcSize)
            circleBackgroundColor.value = ytOverlay.circleBackgroundColor
            circleExpandDuration.value = ytOverlay.animationDuration
            fontSize.value = DataAndUtils.pxToSp(this@MainActivity, ytOverlay.secondsTextView.textSize)
            typeFace.value = Typeface.NORMAL
            iconSpeed.value = ytOverlay.iconAnimationDuration
        }

        viewModel.circleExpandDuration.observe(this, {
            ytOverlay.animationDuration(it)
        })
        viewModel.arcSize.observe(this, {
            ytOverlay.arcSize(DataAndUtils.dpToPx(this@MainActivity, it.toFloat()))
        })
        viewModel.fontSize.observe(this, {
            TextViewStyler().textSize(it).applyTo(ytOverlay.secondsTextView)
        })
        viewModel.typeFace.observe(this, {
            TextViewStyler().textStyle(it).applyTo(ytOverlay.secondsTextView)
        })
        viewModel.tapCircleColor.observe(this, {
            ytOverlay.tapCircleColorInt(it)
        })
        viewModel.circleBackgroundColor.observe(this, {
            ytOverlay.circleBackgroundColorInt(it)
        })
        viewModel.secondsIcon.observe(this, {
            ytOverlay.icon(it)
        })
        viewModel.iconSpeed.observe(this, {
            ytOverlay.iconAnimationDuration(it)
        })
    }

    private fun startNextVideo() {
        releasePlayer()
        initializePlayer()
        ytOverlay.player(player!!)

        currentVideoId = (currentVideoId + 1).rem(DataAndUtils.videoList.size)
        buildMediaSource(Uri.parse(DataAndUtils.videoList[currentVideoId]))
        player?.play()
    }

    private fun toggleFullscreen() {
        if (isVideoFullscreen) {
            setFullscreen(false)
            if (supportActionBar != null) {
                supportActionBar?.show()
            }
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            isVideoFullscreen = false
        } else {
            setFullscreen(true)
            if (supportActionBar != null) {
                supportActionBar?.hide()
            }
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            isVideoFullscreen = true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            R.id.menu_main_action_github -> {
                openInBrowser(DataAndUtils.GITHUB_LINK)
                true
            }
            R.id.menu_main_action_change_video -> {
                startNextVideo()
                Toast.makeText(this, "Video has changed", Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    override fun onBackPressed() {
        if (isVideoFullscreen) {
            toggleFullscreen()
            return
        }
        super.onBackPressed()
    }
}