DoubleTapPlayerView (side branch)
=====

This is a side branch of the actual library. There won't be any changes/updates/fixes regarding the seek feature. For the latest versions see the master branch or the release tab.

The branch is created as of issue [#8](https://github.com/vkay94/DoubleTapPlayerView/issues/8) requested by omkar-tenkale. It adds the possibility to use the ripple effect.

# Download

**Project's gradle file:**

```gradle
allprojects {
  repositories {
    ...
    maven { url 'https://jitpack.io' }
  }
}
```

**app's gradle file:**

```gradle
android {
  ...
  compileOptions {
    sourceCompatibility JavaVersion.VERSION_1_8
    targetCompatibility JavaVersion.VERSION_1_8
  }
}

dependencies {
  implementation 'com.github.vkay94:DoubleTapPlayerView:ripple_view-SNAPSHOT'
}
```

# Getting started

Wrap your layout in `DoubleTapInterceptorFrameLayout` and add `DoubleTapRippleView` to it:

```xml
<com.github.vkay94.dtpv.DoubleTapInterceptorFrameLayout 
    android:id="@+id/interceptLayout"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <androidx.constraintlayout.widget.ConstraintLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent">

        <!-- Your other views -->

        <com.github.vkay94.dtpv.DoubleTapRippleView
            android:id="@+id/overlay"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:clickable="false"
            android:focusable="false"
            android:visibility="invisible" />

    </androidx.constraintlayout.widget.ConstraintLayout>

</com.github.vkay94.dtpv.DoubleTapInterceptorFrameLayout>
```

**Activity** (onCreate, Kotlin)

```kotlin
interceptLayout.rippleView(overlay)
interceptLayout.enableDoubleTaps = true
overlay
    // stayDelay is basically the same as doubleTapDelay of DoubleTapPlayerView
    .stayDelay(500)
    // If true, only real double taps are handled in onDoubleTap, otherwise all ongoing are handled
    // (linke YouTubeOverlay)
    .multiDoubleTap(true)
    .performListener(object : DoubleTapRippleView.PerformListener {
        override fun onAnimationStart() {
            overlay.visibility = View.VISIBLE
        }

        override fun onAnimationEnd() {
            overlay.visibility = View.INVISIBLE

            // You have to call this to 'release', so that touches can be passed to the child views. 
            // Once blockTaps is true (double tapped somewhere while enableDoubleTaps is true) 
            // other interactions won't be recognized (for example scrolling)
            interceptLayout.blockTaps = false
        }
    })
    .tapListener(object : DoubleTapRippleView.TapListener {
        override fun onDoubleTap(gravity: Int) {
            // Reacts to double taps. 
            // Values for gravity: { DoubleTapRippleView.LEFT, DoubleTapRippleView.RIGHT }
        }
    })
```

# Additional Notes

`DoubleTapRippleView` is based on the original `YouTubeOverlay`, so it has the same theming xml attributes (colors and arc size).
