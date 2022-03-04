package com.example.service.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat

class LocaleUpdateBroadcast : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_LOCALE_CHANGED) {
            ContextCompat.startForegroundService(
                context,
                Intent(context, LocaleUpdateService::class.java)
            )
        }
    }
}