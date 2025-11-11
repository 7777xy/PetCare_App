package com.example.petcare_app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.petcare_app.data.AppDatabase
import com.example.petcare_app.data.toReminderEntity
import com.example.petcare_app.data.toReminder
import com.example.petcare_app.ui.screens.Reminder
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.petcare_app.ReminderReceiver
import com.example.petcare_app.data.ReminderEntity
import java.text.SimpleDateFormat
import java.util.*

open class ReminderViewModel(private val db: AppDatabase? = null) : ViewModel() {

    open var upcomingReminders by mutableStateOf(listOf<Reminder>())
        private set
    open var pastReminders by mutableStateOf(listOf<Reminder>())
        private set
    open var completedReminders by mutableStateOf(listOf<Reminder>())
        private set

    init {
        viewModelScope.launch { refreshReminders() }
    }

    fun insertReminder(reminderEntity: ReminderEntity) {
        viewModelScope.launch {
            db?.reminderDao()?.insert(reminderEntity)
            refreshReminders()
        }
    }

    open fun addReminder(newReminder: Reminder) {
        viewModelScope.launch {
            db?.reminderDao()?.insert(newReminder.toReminderEntity())
            refreshReminders()
        }
    }

    open fun updateReminder(updatedReminder: Reminder) {
        viewModelScope.launch {
            db?.reminderDao()?.update(updatedReminder.toReminderEntity())
            refreshReminders()
        }
    }

    open fun deleteReminder(reminder: Reminder) {
        viewModelScope.launch {
            db?.reminderDao()?.delete(reminder.toReminderEntity())
            refreshReminders()
        }
    }

    private suspend fun refreshReminders() {
        val all = db?.reminderDao()?.getAll()?.map { it.toReminder() } ?: emptyList()
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        sdf.timeZone = TimeZone.getDefault()
        val nowMs = System.currentTimeMillis()

        val completed = mutableListOf<Reminder>()
        val upcoming = mutableListOf<Reminder>()
        val past = mutableListOf<Reminder>()

        for (reminder in all) {
            if (reminder.completed) {
                completed.add(reminder)
                continue
            }
            val time = try { sdf.parse(reminder.date)?.time } catch (_: Exception) { null }
            if (time == null) {
                upcoming.add(reminder)
            } else if (time < nowMs) {
                past.add(reminder)
            } else {
                upcoming.add(reminder)
            }
        }

        withContext(Dispatchers.Main) {
            upcomingReminders = upcoming.sortedBy { it.date }.toList()
            pastReminders = past.sortedBy { it.date }.toList()
            completedReminders = completed.sortedBy { it.date }.toList()
        }
    }

    open fun markReminderCompleted(reminder: Reminder) {
        android.util.Log.d("ReminderViewModel", "markReminderCompleted called: ${reminder.title}, completed: ${reminder.completed}")
        viewModelScope.launch {
            db?.reminderDao()?.update(reminder.toReminderEntity())
            android.util.Log.d("ReminderViewModel", "Reminder updated in DB, refreshing list...")
            refreshReminders()
            android.util.Log.d("ReminderViewModel", "List refreshed. Upcoming: ${upcomingReminders.size}, Past: ${pastReminders.size}")
        }
    }

    // --- Schedule notification safely ---
    fun scheduleReminder(context: Context, reminder: Reminder) {
        try {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            sdf.timeZone = TimeZone.getDefault()
            val date = try { sdf.parse(reminder.date) } catch (_: Exception) { null } ?: return

            val triggerTime = date.time
            if (triggerTime <= System.currentTimeMillis()) return

            val intent = Intent(context, ReminderReceiver::class.java).apply {
                putExtra("title", reminder.title)
                putExtra("message", "It's time to ${reminder.title.lowercase()}")
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                reminder.id,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
            } else {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
            }

        } catch (_: Exception) { }
    }

    fun cancelReminder(context: Context, reminder: Reminder) {
        try {
            val intent = Intent(context, ReminderReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                reminder.id,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(pendingIntent)
        } catch (_: Exception) { }
    }

    fun deleteReminderAndCancel(context: Context, reminder: Reminder) {
        cancelReminder(context, reminder)
        deleteReminder(reminder)
    }
}
