DoubleTapPlayerView
=====

A simple library to include double tap behavior to ExoPlayer's PlayerView. Created to handle fast forward/rewind behavior.

### Pre-defined Layouts
 
* **YouTube:** Shows an overlay on double tap with forward/rewind animation.
<img src="github/youtube_preview.png" alt="drawing" width="400"/>

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
  implementation 'com.github.vkay94:DoubleTapPlayerView:0.4.1'
}
```

How to use
-------------------

Layout:

```java
<FrameLayout
    ... >

    <!-- Replace ExoPlayer's PlayerView -->
    <com.github.vkay94.dtpv.DoubleTapPlayerView
            android:id="@+id/doubletapplayerview"
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
            android:layout_height="match_parent"/>

</FrameLayout>
```

Activity (Kotlin): 

```java
// Link the PlayerView to the overlay to pass increment to the Player (seekTo)
// Important: set the (Simple)ExoPlayer to the PlayerView before this call
youTubeDoubleTap
    .setPlayer(doubletapplayerview)
    .setForwardRewindIncrementMs(5000)

// Set YouTube overlay to the PlayerView and double tapping enabled (false by default)
doubletapplayerview
    .activateDoubleTap(true)
    .setDoubleTapDelay(500)
    .setDoubleTapListener(youTubeDoubleTap)
  //.setDoubleTapListener(this) => handle event directly within´the activity
```
