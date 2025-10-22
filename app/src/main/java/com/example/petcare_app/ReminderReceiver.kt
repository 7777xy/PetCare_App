package com.example.petcare_app

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.petcare_app.utils.NotificationHelper

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("title") ?: "Reminder"
        val message = intent.getStringExtra("message") ?: "You have a pet care task!"
        NotificationHelper.showNotification(context, title, message)
    }
}
