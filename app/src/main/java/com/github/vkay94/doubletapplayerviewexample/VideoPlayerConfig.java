package com.github.vkay94.doubletapplayerviewexample;

public class VideoPlayerConfig {

    //Minimum Video you want to buffer while Playing
    public static final int MIN_BUFFER_DURATION = 15000;
    //Max Video you want to buffer during PlayBack
    public static final int MAX_BUFFER_DURATION = 60000;
    //Min Video you want to buffer before start Playing it
    public static final int MIN_PLAYBACK_START_BUFFER = 2500;
    //Min video You want to buffer when username resumes video
    public static final int MIN_PLAYBACK_RESUME_BUFFER = 5000;
}
