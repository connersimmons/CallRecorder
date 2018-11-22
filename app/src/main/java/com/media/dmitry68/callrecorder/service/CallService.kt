package com.media.dmitry68.callrecorder.service

import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import com.media.dmitry68.callrecorder.receiver.CallReceiver
import com.media.dmitry68.callrecorder.receiver.IntentActions

class CallService : Service(){
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        startCallReceiver()
        return Service.START_REDELIVER_INTENT
    }

    private fun startCallReceiver() {
        val callReceiver = CallReceiver()
        val intentFilterPhoneStateChange = IntentFilter(IntentActions.PHONE_STAGE_CHANGED)

        registerReceiver(callReceiver, intentFilterPhoneStateChange)
    }
}