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

class ReminderViewModel(private val db: AppDatabase) : ViewModel() {
    var upcomingReminders by mutableStateOf(listOf<Reminder>())
        private set
    var pastReminders by mutableStateOf(listOf<Reminder>())
        private set
    var completedReminders by mutableStateOf(listOf<Reminder>())
        private set

    init {
        viewModelScope.launch {
            refreshReminders()
        }
    }

    // Used for Add Reminder in appointment screen
    fun insertReminder(reminderEntity: ReminderEntity) {
        viewModelScope.launch {
            db.reminderDao().insert(reminderEntity) // suspend function
            refreshReminders()
        }
    }

    fun addReminder(newReminder: Reminder) {
        viewModelScope.launch {
            db.reminderDao().insert(newReminder.toReminderEntity())
            refreshReminders()
        }
    }

    fun updateReminder(updatedReminder: Reminder) {
        viewModelScope.launch {
            db.reminderDao().update(updatedReminder.toReminderEntity())
            refreshReminders()
        }
    }

    fun deleteReminder(reminder: Reminder) {
        viewModelScope.launch {
            db.reminderDao().delete(reminder.toReminderEntity())
            refreshReminders()
        }
    }

    private suspend fun refreshReminders() {
        val all = db.reminderDao().getAll().map { it.toReminder() }
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
                // If parse fails, treat as upcoming to avoid hiding items
                upcoming.add(reminder)
            } else if (time < nowMs) {
                past.add(reminder)
            } else {
                upcoming.add(reminder)
            }
        }

        // Update state on Main dispatcher and create new list instances to ensure recomposition
        withContext(Dispatchers.Main) {
            upcomingReminders = upcoming.sortedBy { it.date }.toList()
            pastReminders = past.sortedBy { it.date }.toList()
            completedReminders = completed.sortedBy { it.date }.toList()
        }
    }

    fun markReminderCompleted(reminder: Reminder) {
        android.util.Log.d("ReminderViewModel", "markReminderCompleted called: ${reminder.title}, completed: ${reminder.completed}")
        viewModelScope.launch {
            db.reminderDao().update(reminder.toReminderEntity())
            android.util.Log.d("ReminderViewModel", "Reminder updated in DB, refreshing list...")
            refreshReminders()
            android.util.Log.d("ReminderViewModel", "List refreshed. Upcoming: ${upcomingReminders.size}, Past: ${pastReminders.size}")
        }
    }

    // --- Schedule notification safely ---
    fun scheduleReminder(context: Context, reminder: Reminder) {
        try {
            android.util.Log.d(
                "ReminderViewModel",
                "Scheduling reminder: ${reminder.title} for ${reminder.date}"
            )

            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            sdf.timeZone = TimeZone.getDefault() // important
            val date = try {
                sdf.parse(reminder.date)
            } catch (e: Exception) {
                android.util.Log.e("ReminderViewModel", "Failed to parse date: ${reminder.date}", e)
                null
            }
            if (date == null) return

            val triggerTime = date.time
            if (triggerTime <= System.currentTimeMillis()) {
                android.util.Log.w("ReminderViewModel", "Skipping past reminder: ${reminder.title}")
                return // skip past reminders
            }

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

            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
                    android.util.Log.w(
                        "ReminderViewModel",
                        "Cannot schedule exact alarms - using inexact alarm instead"
                    )
                    // Fallback to inexact alarm
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerTime,
                        pendingIntent
                    )
                    android.util.Log.d("ReminderViewModel", "Inexact alarm scheduled for ${reminder.title}")
                } else {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerTime,
                        pendingIntent
                    )
                    android.util.Log.d(
                        "ReminderViewModel",
                        "Exact alarm scheduled successfully for ${reminder.title}"
                    )
                }
            } catch (e: SecurityException) {
                android.util.Log.e("ReminderViewModel", "Security exception scheduling alarm", e)
            }

        } catch (e: Exception) {
            android.util.Log.e("ReminderViewModel", "Error scheduling reminder", e)
        }
    }

    // --- Cancel a scheduled reminder ---
    fun cancelReminder(context: Context, reminder: Reminder) {
        try {
            android.util.Log.d("ReminderViewModel", "Canceling reminder: ${reminder.title}")
            val intent = Intent(context, ReminderReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                reminder.id,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(pendingIntent)
            android.util.Log.d("ReminderViewModel", "Alarm canceled for ${reminder.title}")
        } catch (e: Exception) {
            android.util.Log.e("ReminderViewModel", "Error canceling reminder", e)
        }
    }

    // Convenience: delete from DB and cancel alarm
    fun deleteReminderAndCancel(context: Context, reminder: Reminder) {
        cancelReminder(context, reminder)
        deleteReminder(reminder)
    }
}