package com.github.vkay94.doubletapplayerviewexample

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.TypedValue

object DataAndUtils {

    /**
     * This is a selected list of sample videos for demonstration.
     *
     * Source: [Github Gist](https://gist.github.com/jsturgis/3b19447b304616f18657)
     */
    val videoList = listOf(
        "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
        "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
        "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4",
        "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4"
    )

    const val GITHUB_LINK = "https://github.com/vkay94/DoubleTapPlayerView"

    fun dpToPx(context: Context, dp: Float) =
        dp * context.resources.displayMetrics.density

    fun pxToDp(context: Context, px: Float) =
        px / context.resources.displayMetrics.density

    fun spToPx(context: Context, sp: Float) =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.resources.displayMetrics)

    fun pxToSp(context: Context, px: Float) =
        px / context.resources.displayMetrics.scaledDensity
}


fun Activity.openInBrowser(url: String) {
    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
}