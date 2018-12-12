package com.media.dmitry68.callrecorder.notification

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.support.v4.app.NotificationCompat
import android.support.v4.app.TaskStackBuilder
import com.media.dmitry68.callrecorder.MainActivity
import com.media.dmitry68.callrecorder.R

class NotifyManager(private val context: Context) {

    fun builder() : NotificationCompat.Builder {
        val builder =
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
        builder.setContentIntent(resultPendingIntent)
        return builder
    }
    companion object {
        const val NOTIFICATION_ID = 1
    }
}