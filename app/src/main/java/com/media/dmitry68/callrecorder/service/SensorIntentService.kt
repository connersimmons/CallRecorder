package com.media.dmitry68.callrecorder.service

import android.app.IntentService
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import com.media.dmitry68.callrecorder.notification.NotifyManager

class SensorIntentService: IntentService("SensorIntentService"){
    private val TAG = "LOG"

    override fun onHandleIntent(intent: Intent?) {
        if (intent!!.action == START_SENSOR_SERVICE){
            Log.d(TAG, "Start push notification")
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE)
            val notification = NotifyManager(applicationContext).builder()
            if (notificationManager is NotificationManager)
                notificationManager.notify(NotifyManager.NOTIFICATION_ID, notification.build())
        }
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        Log.d(TAG, "Intent Service on task remove")
        super.onTaskRemoved(rootIntent)
    }

    override fun onDestroy() {
        Log.d(TAG, "Intent Service destroy")
        super.onDestroy()
    }

    private fun sendIntentOnFinishJob() {
        Log.d(TAG, "Send intent JOB_FINISH")
        LocalBroadcastManager.getInstance(this).sendBroadcast(Intent(CallJobService.JOB_FINISH))
    }

    companion object {
        const val START_SENSOR_SERVICE = "com.media.dmitry68.callrecorder.service.STARTSENSORSERVICE"
    }

}