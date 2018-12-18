package com.media.dmitry68.callrecorder.stopwatch

import android.os.Handler
import com.media.dmitry68.callrecorder.notification.NotifyManager

class StopwatchManager {
    companion object {
        private val handler = Handler()
        private lateinit var stopwatch: Stopwatch

        fun start(notifyManager: NotifyManager){
            stopwatch = Stopwatch(notifyManager, handler)
            handler.post(stopwatch)
        }

        fun stop(){
            handler.removeCallbacks(stopwatch)
        }
    }
}