DoubleTapPlayerView
=====

A simple library to include double tap behavior to ExoPlayer's PlayerView. 
Created to handle fast forward/rewind behavior like YouTube.

<p align="center">
    <img src="github/youtube_preview.png" alt="youtube_preview" width="350"/>
</p>

Download
--------
Gradle:

```gradle
allprojects {
  repositories {
    ...
    maven { url 'https://jitpack.io' }
  }
}

dependencies {
  implementation 'com.github.vkay94:DoubleTapPlayerView:0.7.0'
}
```

Usage
-------------------

**View**

```xml
<com.github.vkay94.dtpv.youtube.YouTubeOverlay
     android:id="@+id/youTubeOverlay"
     android:layout_width="match_parent"
     android:layout_height="match_parent" 
     app:yt_playerView="@+id/playerView"
     />
```

**Custom attributes**

| Attribute | Description | Type |
| --------- | ----------- |------|
| **`yt_ffrDuration`** | Fast forward/rewind skip per tap | `integer` *[time in ms]* |
| **`yt_animationDuration`** | Speed of the circle scaling / time to expand completely | `integer` *[time in ms]* |
| **`yt_arcSize`** | Sets the shape, the higher the value the more roundish it becomes | `dimen` |
| **`yt_tapCircleColor`** | Color of the circle which expands | `color` |
| **`yt_ffrDuration`** | Background color of the arc shape | `color` |

Every attribute can also be get/set by Getter/Setter within code. 
In the demo app you can test different values for these attributes.

**Activity (Kotlin)** 

```kotlin
youTubeOverlay.apply {
    performListener = object : YouTubeOverlay.PerformListener {
        override fun onAnimationStart() {
            // Do UI changes when double tapping starts (e.g. hide controller)
            youtubeDoubleTap.visibility = View.VISIBLE
        }

        override fun onAnimationEnd() {
            // Do UI changes when double tapping ends (e.g. show controller)
            youtubeDoubleTap.visibility = View.GONE
        }
    }
}    
```

For a full example implementation take a look at the [VideoActivity][VideoActivity] in the demo app.

[PlayerDoubleTapListener]: https://github.com/vkay94/DoubleTapPlayerView/blob/master/doubletapplayerview/src/main/java/com/github/vkay94/dtpv/PlayerDoubleTapListener.java
[MainActivity]: https://github.com/vkay94/DoubleTapPlayerView/blob/master/app/src/main/java/com/github/vkay94/doubletapplayerviewexample/MainActivity.kt
[VideoActivity]: https://github.com/vkay94/DoubleTapPlayerView/blob/dev/app/src/main/java/com/github/vkay94/doubletapplayerviewexample/VideoActivity.kt