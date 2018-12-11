package com.media.dmitry68.callrecorder.shaker

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener

class ShakeDetector : SensorEventListener {
    lateinit var shakeListener: ShakeListener


    override fun onSensorChanged(event: SensorEvent?) {
        
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }
}