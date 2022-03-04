package com.example.service.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class MessageGlobalBroadcast : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "com.example.service.broadcast.GLOBAL_MESSAGE") {
            Log.d(this.javaClass.simpleName, "message sent")
        }
    }
}