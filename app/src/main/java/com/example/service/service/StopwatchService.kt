package com.example.service.service

import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import com.example.service.notification.NotificationHelper
import kotlinx.coroutines.*

class StopwatchService : Service() {

    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val notificationHelper by lazy { NotificationHelper(this) }

    private var serviceState: StopwatchState? = null
    private var currentTime: Int = 0

    private var timerJob: Job? = null

    override fun onBind(intent: Intent): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        isServiceRunning = true
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        when (intent?.getSerializableExtra(STOPWATCH_SERVICE_COMMAND)) {
            StopwatchState.START -> startTimer()
            StopwatchState.PAUSE -> pauseTimerService()
            StopwatchState.STOP -> endTimerService()
        }

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
        isServiceRunning = false
    }

    private fun startTimer() {
        serviceState = StopwatchState.START

        currentTime = 0

        startForeground(NOTIFICATION_ID, notificationHelper.notificationBuilder.build())
        updateServiceState()

        startCoroutineTimer()
    }

    private fun updateServiceState() {
        when (serviceState) {
            StopwatchState.START -> {
                sendBroadcast(
                    Intent(INTENT_ACTION)
                        .putExtra(STOPWATCH_VALUE, currentTime)
                )
                notificationHelper.makeNotificationWithText(
                    NOTIFICATION_ID,
                    "Current: ${currentTime.secondsToTime()}"
                )
            }
            StopwatchState.PAUSE -> {
                notificationHelper.makeNotificationWithText(
                    NOTIFICATION_ID,
                    "Service paused"
                )
            }
            StopwatchState.STOP -> {
                stopService()
            }
        }
    }

    private fun pauseTimerService() {
        serviceState = StopwatchState.PAUSE
        timerJob?.cancel()
        updateServiceState()
    }

    private fun endTimerService() {
        serviceState = StopwatchState.STOP
        timerJob?.cancel()
        updateServiceState()
    }

    private fun stopService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            stopForeground(true)
        } else {
            stopSelf()
        }
    }

    private fun startCoroutineTimer() {
        timerJob = coroutineScope.launch {
            while (true) {
                currentTime++
                delay(1000)
                updateServiceState()
            }
        }
    }

    companion object {
        private const val NOTIFICATION_ID = 11

        const val INTENT_ACTION = "IntentAction"
        const val STOPWATCH_SERVICE_COMMAND = "Command"
        const val STOPWATCH_VALUE = "StopwatchValue"

        var isServiceRunning = false
            private set
    }
}