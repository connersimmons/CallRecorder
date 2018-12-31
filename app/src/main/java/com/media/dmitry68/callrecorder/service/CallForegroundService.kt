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
    private val innerReceiver = ReceiverOfManageShakeDetector()
    private val TAG = "LOG"
    private var sensorManager: SensorManager? = null
    private lateinit var notifyManager: NotifyManager
    private lateinit var serviceOnDemandManager: ServiceOnDemandManager
    private lateinit var shakeDetector: ShakeDetector
    private lateinit var prefManager: ManagerPref
    private lateinit var state: ModeOfWork
    private lateinit var localBroadcastManager: LocalBroadcastManager

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Service: onDestroy")
        stopCallReceiver()
        stopListenerAndInnerReceiver()
        prefManager.setStateService(false)
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        prefManager.setStateService(false)
        Log.d(TAG, "Service on Task removed")
    }

    override fun onCreate() {
        super.onCreate()
        prefManager = ManagerPref(applicationContext)
        localBroadcastManager = LocalBroadcastManager.getInstance(applicationContext)
        Log.d(TAG, "Service onCreate")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        when (intent?.action) {
            START_FOREGROUND_AUTO_CALL_RECORD_ACTION -> {
                Log.d(TAG, "onStartCommand: START_FOREGROUND_AUTO_CALL_RECORD_ACTION")
                state = ModeOfWork.Background
                startCallReceiver()
                startNotification()
            }
            STOP_FOREGROUND_AUTO_CALL_RECORD_ACTION -> {
                Log.d(TAG, "onStartCommand: STOP_FOREGROUND_AUTO_CALL_RECORD_ACTION")
                state = ModeOfWork.Background
                stopForegroundService()
            }
            START_FOREGROUND_ON_DEMAND_RECORD_ACTION -> {
                Log.d(TAG, "onStartCommand: START_FOREGROUND_ON_DEMAND_RECORD_ACTION")
                state = ModeOfWork.OnDemand
                startNotification()
                initOnDemandManagers()
                startShakeDetector()
            }
            STOP_FOREGROUND_ON_DEMAND_RECORD_ACTION -> { //TODO: add stop record(if record start)(!!!!)
                Log.d(TAG, "onStartCommand: STOP_FOREGROUND_ON_DEMAND_RECORD_ACTION")
                state = ModeOfWork.OnDemand
                stopForegroundService()
            }
        }
        return START_REDELIVER_INTENT
    }

    private fun initOnDemandManagers(){
        serviceOnDemandManager = ServiceOnDemandManager(applicationContext, notifyManager)
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
        Log.d(TAG, "Service register Call receiver")
    }

    private fun startShakeDetector() {
        localBroadcastManager.registerReceiver(innerReceiver, IntentFilter(START_REGISTER_SHAKE_DETECTOR)
            .apply {
                addAction(STOP_REGISTER_SHAKE_DETECTOR)
                addAction(START_CALL_RECEIVER)
                addAction(STOP_CALL_RECEIVER)
        })
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        shakeDetector = ShakeDetector(serviceOnDemandManager)
        registerShakeDetector()
    }

    private fun registerShakeDetector(){
        Log.d(TAG, "Start register shake detector")
        val accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorManager?.registerListener(shakeDetector, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
    }

    private fun stopListenerAndInnerReceiver(){
        Log.d(TAG, "Stop listeners and inner receivers")
        localBroadcastManager.unregisterReceiver(innerReceiver)
        unRegisterShakeDetector()
    }

    private fun stopForegroundService(){
        Log.d(TAG, "Stop foreground Service")
        stopForeground(true)
        stopSelf()
        localBroadcastManager.sendBroadcast(Intent(STOP_FOREGROUND_SERVICE))
    }

    private fun stopCallReceiver() {
        if (callReceiver != null)
            unregisterReceiver(callReceiver)
        Log.d(TAG, "Service unregister receiver")
    }

    private fun unRegisterShakeDetector(){
        Log.d(TAG, "Unregister shake detector")
        sensorManager?.unregisterListener(shakeDetector)
    }

    companion object {
        const val STOP_FOREGROUND_SERVICE = "com.media.dmitry68.callrecorder.service.STOP_FOREGROUND_SERVICE"

        const val START_FOREGROUND_AUTO_CALL_RECORD_ACTION = "com.media.dmitry68.callrecorder.service.START_FOREGROUND_AUTO_CALL_RECORD"
        const val STOP_FOREGROUND_AUTO_CALL_RECORD_ACTION = "com.media.dmitry68.callrecorder.service.STOP_FOREGROUND_AUTO_CALL_RECORD"

        const val START_FOREGROUND_ON_DEMAND_RECORD_ACTION = "com.media.dmitry68.callrecorder.service.START_FOREGROUND_ON_DEMAND_RECORD_ACTION"
        const val STOP_FOREGROUND_ON_DEMAND_RECORD_ACTION = "com.media.dmitry68.callrecorder.service.STOP_FOREGROUND_ON_DEMAND_RECORD_ACTION"

        const val START_REGISTER_SHAKE_DETECTOR = "com.media.dmitry68.callrecorder.service.START_REGISTER_SHAKE_DETECTOR"
        const val STOP_REGISTER_SHAKE_DETECTOR = "com.media.dmitry68.callrecorder.service.STOP_REGISTER_SHAKE_DETECTOR"

        const val START_CALL_RECEIVER = "com.media.dmitry68.callrecorder.service.START_CALL_RECEIVER"
        const val STOP_CALL_RECEIVER = "com.media.dmitry68.callrecorder.service.STOP_CALL_RECEIVER"
    }

    inner class ReceiverOfManageShakeDetector : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            when(intent?.action) {
                START_CALL_RECEIVER -> {
                    startCallReceiver()
                }
                STOP_CALL_RECEIVER -> {
                    stopCallReceiver()
                }
                START_REGISTER_SHAKE_DETECTOR -> {
                    registerShakeDetector()
                }
                STOP_REGISTER_SHAKE_DETECTOR -> {
                    Log.d(TAG, "Stop register shake detector")
                    unRegisterShakeDetector()
                }
            }
        }
    }
}