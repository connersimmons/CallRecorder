package com.media.dmitry68.callrecorder.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.support.v4.app.NotificationCompat
import android.support.v4.app.TaskStackBuilder
import com.media.dmitry68.callrecorder.MainActivity
import com.media.dmitry68.callrecorder.R

class NotifyManager(private val context: Context) {
    private lateinit var notificationBuilder: NotificationCompat.Builder
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun builder() : NotificationCompat.Builder {
        notificationBuilder=
            NotificationCompat.Builder(context, context.getString(com.media.dmitry68.callrecorder.R.string.channel_id))
                .setContentTitle(context.getString(com.media.dmitry68.callrecorder.R.string.notification_title))
                .setContentText(context.getString(com.media.dmitry68.callrecorder.R.string.notification_text))
                .setSmallIcon(android.R.drawable.ic_notification_overlay)
                .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher))

        val resultIntent = Intent(context, MainActivity::class.java)
        val stackBuilder = TaskStackBuilder.create(context).apply {
            addParentStack(MainActivity::class.java)
            addNextIntent(resultIntent)
        }
        val resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        notificationBuilder.setContentIntent(resultPendingIntent)
        return notificationBuilder
    }

    fun addAction(actionIntent: String){
        val intentForBroadcast = Intent(actionIntent)
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intentForBroadcast, PendingIntent.FLAG_UPDATE_CURRENT)
        notificationBuilder.addAction(NotificationCompat.Action(android.R.drawable.ic_notification_overlay,
            context.getString(com.media.dmitry68.callrecorder.R.string.notification_action_stop_recorder),
            pendingIntent))
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }

    fun removeAction(){
        notificationBuilder.mActions.clear()
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }

    companion object {
        const val NOTIFICATION_ID = 1
    }
}