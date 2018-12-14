package com.media.dmitry68.callrecorder.service

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.IBinder
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import com.media.dmitry68.callrecorder.notification.NotifyManager
import com.media.dmitry68.callrecorder.preferences.ManagerPref
import com.media.dmitry68.callrecorder.receiver.CallReceiver
import com.media.dmitry68.callrecorder.receiver.IntentActions
import com.media.dmitry68.callrecorder.shaker.ShakeDetector

class CallForegroundService : Service(){
    private var callReceiver: CallReceiver? = null
    private val innerReceiver = ReceiverOfStopRegisterShakeDetector()
    private val TAG = "LOG"
    private lateinit var notifyManager: NotifyManager
    private lateinit var sensorManager: SensorManager
    private lateinit var shakeDetector: ShakeDetector
    private lateinit var prefManager: ManagerPref
    private lateinit var state: ModeOfWork

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        when (state) {
            ModeOfWork.Background ->
                stopCallReceiver()
            ModeOfWork.OnDemand -> {
                stopListenerAndInnerReceiver()
                stopForegroundService()
            }
        }
        prefManager = ManagerPref(applicationContext)
        prefManager.setStateService(false)
        super.onDestroy()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        Log.d(TAG, "Service on Task removed")
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Service create")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        when (intent?.action) {
            START_FOREGROUND_AUTO_CALL_RECORD_ACTION -> {
                state = ModeOfWork.Background
                startCallReceiver()
                startNotification()
            }
            STOP_FOREGROUND_AUTO_CALL_RECORD_ACTION -> {
                state = ModeOfWork.Background
                stopForegroundService()
            }
            START_FOREGROUND_ON_DEMAND_RECORD_ACTION -> {
                state = ModeOfWork.OnDemand
                Log.d(TAG, "Register shake detector")
                startNotification()
                startShakeDetector()
            }
            STOP_FOREGROUND_ON_DEMAND_RECORD_ACTION -> {
                state = ModeOfWork.OnDemand
                stopListenerAndInnerReceiver()
                stopForegroundService()
            }
        }
        return START_REDELIVER_INTENT
    }

    private fun startNotification(){
        notifyManager = NotifyManager(applicationContext)
        val notificationBuilder = notifyManager.builder().build()
        startForeground(NotifyManager.NOTIFICATION_ID, notificationBuilder)
    }

    private fun startCallReceiver() {
        if (callReceiver == null)
            callReceiver = CallReceiver()
        val intentFilterPhoneStateChange = IntentFilter(IntentActions.PHONE_STAGE_CHANGED)
        registerReceiver(callReceiver, intentFilterPhoneStateChange)
        Log.d(TAG, "Service register receiver")
    }

    private fun startShakeDetector() {
        LocalBroadcastManager.getInstance(this).registerReceiver(innerReceiver, IntentFilter(STOP_REGISTER_SHAKE_DETECTOR))
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        shakeDetector = ShakeDetector(applicationContext, notifyManager)
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorManager.registerListener(shakeDetector, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
    }

    private fun stopListenerAndInnerReceiver(){
        LocalBroadcastManager.getInstance(this).unregisterReceiver(innerReceiver)
        unRegisterShakeDetector()
    }

    private fun stopForegroundService(){
        Log.d(TAG, "Stop foreground Service")
        stopForeground(true)
        stopSelf()
    }

    private fun stopCallReceiver() {
        if (callReceiver != null)
            unregisterReceiver(callReceiver)
        Log.d(TAG, "Service unregister receiver")
    }

    private fun unRegisterShakeDetector(){
        Log.d(TAG, "Unregister shake detector")
        sensorManager.unregisterListener(shakeDetector)
    }

    companion object {
        const val START_FOREGROUND_AUTO_CALL_RECORD_ACTION = "com.media.dmitry68.callrecorder.service.START_FOREGROUND_AUTO_CALL_RECORD"
        const val STOP_FOREGROUND_AUTO_CALL_RECORD_ACTION = "com.media.dmitry68.callrecorder.service.STOP_FOREGROUND_AUTO_CALL_RECORD"
        const val START_FOREGROUND_ON_DEMAND_RECORD_ACTION = "com.media.dmitry68.callrecorder.service.START_FOREGROUND_ON_DEMAND_RECORD_ACTION"
        const val STOP_FOREGROUND_ON_DEMAND_RECORD_ACTION = "com.media.dmitry68.callrecorder.service.STOP_FOREGROUND_ON_DEMAND_RECORD_ACTION"
        const val STOP_REGISTER_SHAKE_DETECTOR = "com.media.dmitry68.callrecorder.service.STOP_REGISTER_SHAKE_DETECTOR"
    }

    inner class ReceiverOfStopRegisterShakeDetector : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d(TAG, "Stop register shake detector")
            unRegisterShakeDetector()
        }
    }
}