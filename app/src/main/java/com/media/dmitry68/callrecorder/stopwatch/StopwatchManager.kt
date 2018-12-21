package com.media.dmitry68.callrecorder.stopwatch

import android.os.Handler
import com.media.dmitry68.callrecorder.notification.NotifyManager

class StopwatchManager(private val notifyManager: NotifyManager) : Handler(){
    private lateinit var stopwatch: Stopwatch
    fun start(){
        notifyManager.addText()
        stopwatch = Stopwatch(this)
        post(stopwatch)
    }


    fun stop(){
        removeCallbacks(stopwatch)
    }
}