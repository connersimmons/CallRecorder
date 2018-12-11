package com.media.dmitry68.callrecorder.service

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorManager
import android.util.Log
import com.media.dmitry68.callrecorder.shaker.ShakeDetector
import com.media.dmitry68.callrecorder.shaker.ShakeListener

class SensorIntentService: IntentService("SensorIntentService"){
    private val TAG = "LOG"

    override fun onHandleIntent(intent: Intent?) {
        if (intent!!.action == START_SENSOR_SERVICE){
            Log.d(TAG, "Start sensor service")
            startShakeDetector()
        }
    }

    private fun startShakeDetector() {
        val sensorManager = getSystemService(Context.SENSOR_SERVICE)
        if (sensorManager is SensorManager){
            val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            val shakeDetector = ShakeDetector()
            shakeDetector.shakeListener = object : ShakeListener {
                override fun onShake(count: Int) {
                    Log.d(TAG, "Detect $count shake")
                }
            }
            sensorManager.registerListener(shakeDetector, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    companion object {
        const val START_SENSOR_SERVICE = "com.media.dmitry68.callrecorder.service.STARTSENSORSERVICE"
    }

}