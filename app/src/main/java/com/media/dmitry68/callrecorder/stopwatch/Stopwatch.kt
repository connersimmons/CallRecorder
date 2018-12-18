package com.media.dmitry68.callrecorder.stopwatch

import android.os.Handler
import android.os.SystemClock
import com.media.dmitry68.callrecorder.notification.NotifyManager


class Stopwatch(private val notifyManager: NotifyManager,
                private val handler: Handler? = null
): Runnable {
    private var millisecondTime = 0L
    private var startTime = SystemClock.uptimeMillis()
    private var timeBuff = 0L
    private var updateTime = 0L

    private var seconds = 0
    private var minutes = 0
    private var milliseconds = 0
    private var hours = 0

    private var secondsText: String? = null
    private var minutesText: String? = null
    private var hourText: String? = null

    private var stopwatchText: String? = null

    override fun run() {
        millisecondTime = SystemClock.uptimeMillis() - startTime
        updateTime = timeBuff + millisecondTime

        seconds = (updateTime / 1000).toInt()
        minutes = seconds / 60
        hours = minutes / 60
        seconds %= 60
        milliseconds = (updateTime % 1000).toInt()


        secondsText =
                if (seconds.toString().length < 2){
                    "0" + seconds.toString() + ":"
                } else {
                    seconds.toString() + ":"
                }
        minutesText =
                if (minutes.toString().length < 2){
                    "0" + minutes.toString() + ":"
                } else {
                    minutes.toString() + ":"
                }
        hourText =
                when {
                    hours == 0 -> ""
                    hours.toString().length < 2 -> "0" + hours.toString() + ":"
                    else -> hours.toString() + ":"
                }

        stopwatchText = StringBuilder().apply {
            append(hourText)
            append(minutesText)
            append(secondsText)
        }.toString()

        notifyManager.addText(stopwatchText ?: "")
        handler!!.post(this)
    }
}