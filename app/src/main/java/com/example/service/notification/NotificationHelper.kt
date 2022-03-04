package com.example.service.notification

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.service.MainActivity
import com.example.service.R

class NotificationHelper(private val context: Context) {

    private val notificationManager = NotificationManagerCompat.from(context)

    private val contentIntent: PendingIntent =
        PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

    val notificationBuilder: NotificationCompat.Builder
        get() = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(context.getString(R.string.app_name))
            .setContentIntent(contentIntent)

    init {
        notificationManager.createNotificationChannel(createChannel())
    }

    fun makeNotificationWithText(
        notificationId: Int,
        contentText: String
    ) {
        val notification = notificationBuilder
            .setContentText(contentText)
            .build()
        notificationManager.notify(notificationId, notification)
    }

    fun makeNotificationWithProgress(
        notificationId: Int,
        progress: Int
    ) {
        val notification = notificationBuilder
            .setProgress(100, progress, false)
            .build()
        notificationManager.notify(notificationId, notification)
    }

    private fun createChannel() =
        NotificationChannelCompat.Builder(CHANNEL_ID, NotificationManagerCompat.IMPORTANCE_DEFAULT)
            .setName(CHANNEL_NAME)
            .setDescription(CHANNEL_DESCRIPTION)
            .build()

    companion object {
        private const val CHANNEL_ID = "serviceChannelId"
        private const val CHANNEL_NAME = "Service Channel"
        private const val CHANNEL_DESCRIPTION = "Service channel description"
    }
}