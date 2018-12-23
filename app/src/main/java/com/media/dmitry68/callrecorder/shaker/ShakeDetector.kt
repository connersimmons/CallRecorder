package com.media.dmitry68.callrecorder.shaker

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import com.media.dmitry68.callrecorder.service.ServiceOnDemandManager


class ShakeDetector(private val serviceOnDemandManager: ServiceOnDemandManager) : SensorEventListener, ShakeListener {
    private var shakeTimeStamp = 0L
    private var shakeCount = 0
    private val TAG = "LOG"

    override fun onSensorChanged(event: SensorEvent?) {
        val x = event!!.values[0]
        val y = event.values[1]
        val z = event.values[2]

        val gX = x / SensorManager.GRAVITY_EARTH
        val gY = y / SensorManager.GRAVITY_EARTH
        val gZ = z / SensorManager.GRAVITY_EARTH

        val gForce = Math.sqrt((gX * gX + gY * gY + gZ * gZ).toDouble())

        if (gForce > SHAKE_THRESHOLD_GRAVITY){
            Log.d(TAG, "Sensor detector $shakeCount changes")
            val now = System.currentTimeMillis()
            if (shakeTimeStamp + SHAKE_SLOP_TIME_MS > now)
                return

            if (shakeTimeStamp + SHAKE_COUNT_RESET_TIME_MS < now)
                shakeCount = 0

            shakeTimeStamp = now
            shakeCount++

            onShake(shakeCount)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    override fun onShake(count: Int) {
        Log.d(TAG, "Detect $count shake")
        if (count == 3) {//TODO:make count preference
            serviceOnDemandManager.startRecordOnShakeDetector()
        }
    }

    companion object {
        const val SHAKE_THRESHOLD_GRAVITY = 2.7F
        const val SHAKE_SLOP_TIME_MS = 500
        const val SHAKE_COUNT_RESET_TIME_MS = 3000
    }


}