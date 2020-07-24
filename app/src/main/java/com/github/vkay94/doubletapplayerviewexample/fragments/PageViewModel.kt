package com.github.vkay94.doubletapplayerviewexample.fragments

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PageViewModel : ViewModel() {

    val circleExpandDuration = MutableLiveData<Long>()
    val arcSize = MutableLiveData<Float>()
    val iconSpeed = MutableLiveData<Long>()
    val fontSize = MutableLiveData<Float>()
    val typeFace = MutableLiveData<Int>()
    val secondsIcon = MutableLiveData<Int>()

    val tapCircleColor = MutableLiveData<Int>()
    val circleBackgroundColor = MutableLiveData<Int>()
}