package com.example.petcare_app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

import com.example.petcare_app.data.AppDatabase
import com.example.petcare_app.data.toReminderEntity
import com.example.petcare_app.data.toReminder
import com.example.petcare_app.ui.screens.Reminder

class ReminderViewModel(private val db: AppDatabase) : ViewModel() {
    var reminders by mutableStateOf(listOf<Reminder>())
        private set

    init {
        viewModelScope.launch {
            reminders = db.reminderDao().getAll().map { it.toReminder() }
        }
    }

    fun addReminder(newReminder: Reminder) {
        viewModelScope.launch {
            db.reminderDao().insert(newReminder.toReminderEntity())
            reminders = db.reminderDao().getAll().map { it.toReminder() }
        }
    }

    fun updateReminder(updatedReminder: Reminder) {
        viewModelScope.launch {
            db.reminderDao().update(updatedReminder.toReminderEntity())
            reminders = db.reminderDao().getAll().map { it.toReminder() }
        }
    }

    fun deleteReminder(reminder: Reminder) {
        viewModelScope.launch {
            db.reminderDao().delete(reminder.toReminderEntity())
            reminders = db.reminderDao().getAll().map { it.toReminder() }
        }
    }
}
