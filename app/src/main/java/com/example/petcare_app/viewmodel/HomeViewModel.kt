package com.example.petcare_app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.petcare_app.data.AppDatabase
import com.example.petcare_app.data.toReminder
import com.example.petcare_app.ui.screens.Reminder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(private val db: AppDatabase) : ViewModel() {

    private val _reminders = MutableStateFlow<List<Reminder>>(emptyList())
    val reminders = _reminders.asStateFlow()

    init {
        loadReminders()
    }

    private fun loadReminders() {
        viewModelScope.launch {
            val allReminders = db.reminderDao().getAll().map { it.toReminder() }
            _reminders.value = allReminders.sortedBy { it.date }
        }
    }
}
