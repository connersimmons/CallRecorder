package com.media.dmitry68.callrecorder.service

import android.app.job.JobParameters
import android.app.job.JobService
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.support.v4.content.LocalBroadcastManager
import android.util.Log

class CallJobService: JobService() {
    private val TAG = "LOG"
    private lateinit var jobParameters: JobParameters

    override fun onStartJob(params: JobParameters?): Boolean {
        Log.d(TAG, "Start job with id: ${params!!.jobId}")
        jobParameters = params
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


    companion object {
        const val JOB_FINISH = "com.media.dmitry68.callrecorder.service.JOBFINISH"
    }

    inner class ReceiverOfFinishJobService : BroadcastReceiver(){//this receiver need for finish Job TODO: think how make it better
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent!!.action == JOB_FINISH){
                Log.d(TAG, "Job $jobParameters finished")
                val rescheduleThisJob = false
                LocalBroadcastManager.getInstance(context!!).unregisterReceiver(this)
                jobFinished(jobParameters, rescheduleThisJob)
            }
        }

    }
}