package com.media.dmitry68.callrecorder.service

import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.util.Log
import com.media.dmitry68.callrecorder.notification.NotificationManager
import com.media.dmitry68.callrecorder.receiver.CallReceiver
import com.media.dmitry68.callrecorder.receiver.IntentActions

class CallService : Service(){
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
        startCallReceiver()
        val notification = NotificationManager(this).Builder().build()
        startForeground(NotificationManager.NOTIFICATION_ID, notification)
        Log.d(TAG, "Service create")
    }

    private fun startCallReceiver() {
        if (callReceiver == null)
            callReceiver = CallReceiver()
        val intentFilterPhoneStateChange = IntentFilter(IntentActions.PHONE_STAGE_CHANGED)
        registerReceiver(callReceiver, intentFilterPhoneStateChange)
        Log.d(TAG, "Service register receiver")
    }
}