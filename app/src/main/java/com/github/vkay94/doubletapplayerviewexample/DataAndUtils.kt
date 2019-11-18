package com.github.vkay94.doubletapplayerviewexample

import android.content.Context

object DataAndUtils {

    /**
     * This is a selected list of sample videos for demonstration
     * Found at: https://gist.github.com/jsturgis/3b19447b304616f18657
     */
    val videoList = listOf(
        "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
    )

    fun dpToPx(context: Context, dp: Float) =
        dp * context.resources.displayMetrics.density

    fun pxToDp(context: Context, px: Float) =
        px / context.resources.displayMetrics.density
}