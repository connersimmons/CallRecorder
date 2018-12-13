package com.media.dmitry68.callrecorder.service

import android.app.job.JobParameters
import android.app.job.JobService
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorManager
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import com.media.dmitry68.callrecorder.shaker.ShakeDetector
import com.media.dmitry68.callrecorder.shaker.ShakeListener

class CallJobService: JobService() {
    private val TAG = "LOG"
    private lateinit var jobParameters: JobParameters

    override fun onStartJob(params: JobParameters?): Boolean {
        Log.d(TAG, "Start job with id: ${params!!.jobId}")
        jobParameters = params
        startShakeDetector()
        return true
     }

    override fun onStopJob(params: JobParameters?): Boolean {
        Log.d(TAG, "Stop job with id: ${params!!.jobId}")
        return true
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        Log.d(TAG, "JobService on task remove")
        super.onTaskRemoved(rootIntent)
    }

    override fun onDestroy() {
        Log.d(TAG, "JobService destroy")
        super.onDestroy()
    }

    private fun startShakeDetector() {
        val sensorManager = getSystemService(Context.SENSOR_SERVICE)
        if (sensorManager is SensorManager){
            Log.d(TAG, "Start sensor service")
            val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            val shakeDetector = ShakeDetector()
            shakeDetector.shakeListener = object : ShakeListener {
                override fun onShake(count: Int) {
                    Log.d(TAG, "Detect $count shake")
                    if (count == 3) {//TODO:make count preference
                        startIntentService()
                    }
                }
            }
            sensorManager.registerListener(shakeDetector, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    private fun startIntentService(){
         val sensorIntent = Intent(applicationContext, SensorIntentService::class.java)
            .apply {
                action = SensorIntentService.START_SENSOR_SERVICE
            }
        startService(sensorIntent)
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