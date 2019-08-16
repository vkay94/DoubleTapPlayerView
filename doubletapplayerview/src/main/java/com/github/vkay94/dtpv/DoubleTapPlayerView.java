package com.github.vkay94.dtpv;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.view.GestureDetectorCompat;

import com.google.android.exoplayer2.ui.PlayerView;

/**
 * Custom player class for Double-Tapping listening
 * Performs behavior like YouTube (by default)
 */
public final class DoubleTapPlayerView extends PlayerView {

    public static final String TAG = ".DoubleTapPlayerView";
    public static boolean DEBUG = !BuildConfig.BUILD_TYPE.equals("release");

    private boolean doubleTapActivated = false;

    private GestureDetectorCompat mDetector;

    PlayerDoubleTapListener listener;

    // Variable to save current state
    private boolean isDoubleTap = false;

    private Handler mHandler = new Handler();
    private Runnable mRunnable = () -> {
        Log.d(TAG, "Runnable called");
        isDoubleTap = false;
        listener.onDoubleTapFinished();
    };

    // Variables for YouTube behavior:
    private DoubleTapOverlay controls;

    /**
     * Default time window in which the double tap is "active"
     * Resets if another tap occured within the time window
     **/
    long DOUBLE_TAP_DELAY = 500;

    /**
     * Skip interval per tap in milliseconds
     */
    int FAST_REWIND_FORWARD_SKIP = 10000;

    // Variables for YouTube behavior
    private int currentRewindForward = 0;

    public DoubleTapPlayerView(Context context) {
        this(context, null);
    }

    public DoubleTapPlayerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DoubleTapPlayerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mDetector = new GestureDetectorCompat(context, new DoubleTapGestureListener());
    }

    /**
     * Sets whether the PlayerView recognizes double tap gestures or not
     */
    public DoubleTapPlayerView activateDoubleTap(boolean active) {
        this.doubleTapActivated = active;
        return this;
    }

    /**
     * Method to initialize controller and listeners if controller is set
     * Obligatory for YouTube behavior
     *
     * @param controls Passed controller which is within the activity layout (on top)
     */
    public DoubleTapPlayerView setOverlay(DoubleTapOverlay controls) {
        this.controls = controls;
        initYTController();
        initYTListener();

        return this;
    }

    /**
     * Sets the new skip interval
     */
    public DoubleTapPlayerView setSkipDurationMs(int newValue) {
        this.FAST_REWIND_FORWARD_SKIP = newValue;
        return this;
    }

    /**
     * Sets optional {@link PlayerDoubleTapListener} for custom implementation
     */
    public DoubleTapPlayerView setDoubleTapListener(PlayerDoubleTapListener listener) {
        this.listener = listener;
        return this;
    }

    /**
     * Changes the time window a double tap is active, so a followed tap is calling
     * a gesture detector method instead of normal tap (see {@link PlayerView#onTouchEvent})
     */
    public DoubleTapPlayerView setDoubleTapDelay(int milliSeconds) {
        this.DOUBLE_TAP_DELAY = milliSeconds;
        return this;
    }

    /**
     * Resets the timeout to keep in double tap mode. Can be called from outside if
     * the double tap is customized / overridden
     */
    public void keepInDoubleTapMode() {
        mHandler.removeCallbacks(mRunnable);
        mHandler.postDelayed(mRunnable, DOUBLE_TAP_DELAY);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (doubleTapActivated) {
            mDetector.onTouchEvent(ev);

            // Do not trigger original behavior when double tapping
            // otherwise the controller would show/hide - it would flack
            return true;
        }

        return super.onTouchEvent(ev);
    }

    private void initYTController() {
        controls.getRewindContainer().setOnClickListener(view -> {
            keepInDoubleTapMode();
            currentRewindForward += FAST_REWIND_FORWARD_SKIP / 1000;
            controls.getTvRewind().setText( getResources().getString(R.string.dtp_rf_seconds, currentRewindForward));
            getPlayer().seekTo(getPlayer().getCurrentPosition() - 10000);
        });

        controls.getForwardContainer().setOnClickListener(view -> {
            keepInDoubleTapMode();
            currentRewindForward += FAST_REWIND_FORWARD_SKIP / 1000;
            controls.getTvForward().setText(getResources().getString(R.string.dtp_rf_seconds, currentRewindForward));
            getPlayer().seekTo(getPlayer().getCurrentPosition() + 10000);
        });
    }

    private void initYTListener() {
        listener = new PlayerDoubleTapListener() {

            @Override
            public void onDoubleTapStarted(float posX, float posY) {
                if (DEBUG) Log.d(TAG, "onDoubleTapStarted: " + posX);

                // Hide controls (hideController() wouldn't work correctly if playWhenReady is false
                // (the canvas would flack))
                setUseController(false);
                controls.show();
            }

            @Override
            public void onDoubleTapProgress(float posX, float posY) {
                if (DEBUG) Log.d(TAG, "onDoubleTapProgress: " + posX);

                // Method called when already in double tap mode and tapping area is different
                // (for example: started left and then right/middle)

                currentRewindForward = FAST_REWIND_FORWARD_SKIP / 1000;

                int value;

                if (posX < getWidth() * 0.35) {
                    if (controls.getForwardContainer().getVisibility() == View.VISIBLE)
                        controls.getForwardContainer().setVisibility(View.INVISIBLE);

                    controls.getTvRewind().setText(getResources().getString(R.string.dtp_rf_seconds, currentRewindForward));
                    controls.getRewindContainer().setVisibility(View.VISIBLE);
                    controls.getRewindAnimation().start();

                    value = -1;

                } else if (posX > getWidth() * 0.65) {

                    if (controls.getRewindContainer().getVisibility() == View.VISIBLE)
                        controls.getRewindContainer().setVisibility(View.INVISIBLE);

                    controls.getTvForward().setText(getResources().getString(R.string.dtp_rf_seconds, currentRewindForward));
                    controls.getForwardContainer().setVisibility(View.VISIBLE);
                    controls.getForwardAnimation().start();

                    value = 1;

                } else {
                    mHandler.removeCallbacks(mRunnable);
                    isDoubleTap = false;
                    listener.onDoubleTapFinished();
                    return;
                }

                long newPosition = getPlayer().getCurrentPosition() + value * FAST_REWIND_FORWARD_SKIP;
                getPlayer().seekTo(newPosition);
            }

            @Override
            public void onDoubleTapFinished() {
                if (DEBUG) Log.d(TAG, "onDoubleTapFinished");

                // Hide overlay and re-add the controller to the player and show controls
                // if the player was paused previously
                setUseController(true);
                controls.hide();

                if (!getPlayer().getPlayWhenReady()) showController();

                // Set overlay to its original state
                controls.getRewindContainer().setVisibility(View.INVISIBLE);
                controls.getForwardContainer().setVisibility(View.INVISIBLE);

                controls.getForwardAnimation().stop();
                controls.getRewindAnimation().stop();
            }
        };
    }

    /**
     * Gesture Listener for double tapping
     */
    private class DoubleTapGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            // Used to override the other methods
            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            if (isDoubleTap) {
                if (DEBUG) Log.d(TAG, "onSingleTapUp: isDoubleTap = true");

                // Remove previous runnable and re-add it to reset the time and call listener method
                keepInDoubleTapMode();
                listener.onDoubleTapProgress(e.getX(), e.getY());
            }

            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            // Ignore this event if double tapping is still active
            // Return true needed because this method is also called if you tap e.g. three times
            // in a row, therefore the controller would appear since the original behavior is
            // to hide and show on single tap
            if (isDoubleTap) return true;
            if (DEBUG) Log.d(TAG, "onSingleTapConfirmed: isDoubleTap = false");

            return performClick();
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            // First tap (ACTION_DOWN) of both taps
            if (DEBUG) Log.d(TAG, "onDoubleTap");

            if (!isDoubleTap) {
                isDoubleTap = true;
                keepInDoubleTapMode();
                listener.onDoubleTapStarted(e.getX(), e.getY());
            }
            return true;
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            // Second tap (ACTION_UP) of both taps
            if (e.getActionMasked() == MotionEvent.ACTION_UP && isDoubleTap) {
                if (DEBUG) Log.d(TAG, "onDoubleTapEvent, ACTION_UP");

                keepInDoubleTapMode();
                listener.onDoubleTapProgress(e.getX(), e.getY());

                return true;
            }
            return super.onDoubleTapEvent(e);
        }
    }

    public interface PlayerDoubleTapListener {

        /**
         * Called when double tapping starts
         *
         * @param posX x tap position on the root view
         * @param posY y tap position on the root view
         */
        void onDoubleTapStarted(float posX, float posY);

        /**
         * Called for each ongoing tap when double tap started
         *
         * @param posX x tap position on the root view
         * @param posY y tap position on the root view
         */
        void onDoubleTapProgress(float posX, float posY);

        /**
         * Called when the delay is over (double tap series is finished)
         */
        void onDoubleTapFinished();
    }
}
