package com.media.dmitry68.callrecorder.shaker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import com.media.dmitry68.callrecorder.notification.NotifyManager
import com.media.dmitry68.callrecorder.recorder.Recorder
import com.media.dmitry68.callrecorder.service.CallForegroundService
import com.media.dmitry68.callrecorder.stateCall.Caller

class ShakeDetector(private val appContext: Context,
                    private val notificationManager: NotifyManager) : SensorEventListener, ShakeListener {
    private var shakeTimeStamp = 0L
    private var shakeCount = 0
    private val TAG = "LOG"
    private val innerReceiverForStopRecorder = ReceiverOfStopReorder()
    private lateinit var recorder: Recorder

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
           LocalBroadcastManager.getInstance(appContext).sendBroadcast(Intent(CallForegroundService.STOP_REGISTER_SHAKE_DETECTOR))//TODO: add vibrate
            Log.d(TAG, "Start Record")
            initRecord(appContext)
        }
    }

    private fun initRecord(context: Context) {
        recorder = Recorder(Caller(), context).apply { startRecord() }
        context.registerReceiver(innerReceiverForStopRecorder, IntentFilter(STOP_RECORD_ACTION))

        notificationManager.addAction(STOP_RECORD_ACTION)
    }

    companion object {
        const val SHAKE_THRESHOLD_GRAVITY = 2.7F
        const val SHAKE_SLOP_TIME_MS = 500
        const val SHAKE_COUNT_RESET_TIME_MS = 3000

        const val STOP_RECORD_ACTION = "com.media.dmitry68.callrecorder.shaker.STOP_RECORD_ACTION"
    }

    inner class ReceiverOfStopReorder : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d(TAG, "Stop recorder")
            notificationManager.removeAction()
            recorder.stopRecord()
            appContext.unregisterReceiver(innerReceiverForStopRecorder)
        }
    }
}