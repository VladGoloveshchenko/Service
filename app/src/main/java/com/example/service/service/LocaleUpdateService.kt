package com.example.service.service

import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import com.example.service.notification.NotificationHelper
import kotlinx.coroutines.*

class LocaleUpdateService : Service() {

    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val notificationHelper by lazy { NotificationHelper(this) }

    override fun onBind(p0: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val initialNotification =
            notificationHelper
                .notificationBuilder
                .setProgress(100, 0, false)
                .build()
        startForeground(NOTIFICATION_ID, initialNotification)
        doWork()

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }

    private fun doWork() = coroutineScope.launch {
        repeat(10) {
            delay(500)
            updateNotification(10 * (it + 1))
        }
        delay(500)
        stopService()
    }

    private fun updateNotification(progress: Int) {
        notificationHelper.makeNotificationWithProgress(NOTIFICATION_ID, progress)
    }

    private fun stopService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            stopForeground(true)
        } else {
            stopSelf()
        }
    }

    companion object {
        private const val NOTIFICATION_ID = 22
    }
}