package com.media.dmitry68.callrecorder.service

import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.util.Log
import com.media.dmitry68.callrecorder.notification.NotifyManager
import com.media.dmitry68.callrecorder.receiver.CallReceiver
import com.media.dmitry68.callrecorder.receiver.IntentActions

class CallForegroundService : Service(){
    private var callReceiver: CallReceiver? = null
    private val TAG = "LOG"

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        stopCallReceiver()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        Log.d(TAG, "Service on Task removed")
    }

    private fun stopCallReceiver() {
        if (callReceiver != null)
            unregisterReceiver(callReceiver)
        Log.d(TAG, "Service unregister receiver")
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Service create")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        if (intent?.action.equals(START_FOREGROUND_ACTION)) {
            startCallReceiver()
            val notification = NotifyManager(this).builder().build()
            startForeground(NotifyManager.NOTIFICATION_ID, notification)
        } else if (intent?.action.equals(STOP_FOREGROUND_ACTION)){
            stopForeground(true)
            stopSelf()
        }
        return START_REDELIVER_INTENT
    }

    private fun startCallReceiver() {
        if (callReceiver == null)
            callReceiver = CallReceiver()
        val intentFilterPhoneStateChange = IntentFilter(IntentActions.PHONE_STAGE_CHANGED)
        registerReceiver(callReceiver, intentFilterPhoneStateChange)
        Log.d(TAG, "Service register receiver")
    }

    companion object {
        const val START_FOREGROUND_ACTION = "com.media.dmitry68.callrecorder.service.STARTFOREGROUND"
        const val STOP_FOREGROUND_ACTION = "com.media.dmitry68.callrecorder.service.STOPFOREGROUND"
    }
}