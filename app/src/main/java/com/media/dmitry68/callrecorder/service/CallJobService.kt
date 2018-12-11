package com.media.dmitry68.callrecorder.service

import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Intent
import android.util.Log

class CallJobService: JobService() {
    private val TAG = "LOG"

    override fun onStartJob(params: JobParameters?): Boolean {
        Log.d(TAG, "Start job with id: ${params!!.jobId}")
        val sensorIntent = Intent(applicationContext, SensorIntentService::class.java)
            .apply {
                action = SensorIntentService.START_SENSOR_SERVICE
            }
        startService(sensorIntent)
        return true
     }

    override fun onStopJob(params: JobParameters?): Boolean {
        Log.d(TAG, "Stop job with id: ${params!!.jobId}")
        return true
    }


}