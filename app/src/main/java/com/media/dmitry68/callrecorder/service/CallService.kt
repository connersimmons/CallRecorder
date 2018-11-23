package com.media.dmitry68.callrecorder.service

import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import com.media.dmitry68.callrecorder.receiver.CallReceiver
import com.media.dmitry68.callrecorder.receiver.IntentActions

class CallService : Service(){
    private var callReceiver = CallReceiver()

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        startCallReceiver()
        return Service.START_REDELIVER_INTENT
    }

    override fun onDestroy() {
        super.onDestroy()
        stopCallReceiver()
    }

    private fun stopCallReceiver() {
        unregisterReceiver(callReceiver)
    }

    private fun startCallReceiver() {
        val intentFilterPhoneStateChange = IntentFilter(IntentActions.PHONE_STAGE_CHANGED)
        registerReceiver(callReceiver, intentFilterPhoneStateChange)
    }
}