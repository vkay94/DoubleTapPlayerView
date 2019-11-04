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
  implementation 'com.github.vkay94:DoubleTapPlayerView:0.6.0'
}
```

How to use
-------------------

In the following I describe how to implement and configure the YouTube-alike behavior
([see the sample app for a possible implementation][MainActivity]). 

#### Basic usage

Layout:

```xml
<FrameLayout>

    <!-- Replace ExoPlayer's PlayerView -->
    <com.github.vkay94.dtpv.DoubleTapPlayerView
            android:id="@+id/doubleTapPlayerView"
            app:player_layout_id="@layout/exo_simple_player_view"
            app:use_controller="true"
            ... 
    />

    <!-- Other views e.g. ProgressBar etc  -->

    <!-- Add the overlay on top of PlayerView -->
    <com.github.vkay94.dtpv.YouTubeDoubleTap
            android:background="@color/dtp_overlay_dim"
            android:id="@+id/youTubeDoubleTap"
            android:layout_width="match_parent"
            android:layout_height="match_parent" />

</FrameLayout>
```

Activity (Kotlin): 

```kotlin
// Link the PlayerView to the overlay to pass increment to the Player (seekTo)
// Important: set the (Simple)ExoPlayer to the PlayerView before this call
youTubeDoubleTap
    .setPlayer(doubletapplayerview)
    .setForwardRewindIncrementMs(5000)

// Set YouTube overlay to the PlayerView and double tapping enabled (false by default)
doubleTapPlayerView
    .activateDoubleTap(true)
    .setDoubleTapDelay(500)
    .setDoubleTapListener(youTubeDoubleTap)
```

#### Additional features

* **SeekListener**: By implementing this interface you can react to the 
                    events *onVideoStartReached* and *onVideoEndReached*          
* **Toogle via code**: You can simulate a double tap programmatically e.g. 
                    when a button clicked 

[PlayerDoubleTapListener]: https://github.com/vkay94/DoubleTapPlayerView/blob/master/doubletapplayerview/src/main/java/com/github/vkay94/dtpv/PlayerDoubleTapListener.java
[MainActivity]: https://github.com/vkay94/DoubleTapPlayerView/blob/master/app/src/main/java/com/github/vkay94/doubletapplayerviewexample/MainActivity.kt