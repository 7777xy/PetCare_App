package com.example.petcare_app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.petcare_app.data.AppDatabase
import com.example.petcare_app.data.toAppointment
import com.example.petcare_app.data.toReminder
import com.example.petcare_app.ui.screens.Appointment
import com.example.petcare_app.ui.screens.Reminder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(private val db: AppDatabase) : ViewModel() {

    private val _appointments = MutableStateFlow<List<Appointment>>(emptyList())
    val appointments = _appointments.asStateFlow()

    private val _reminders = MutableStateFlow<List<Reminder>>(emptyList())
    val reminders = _reminders.asStateFlow()

    init {
        loadAppointments()
        loadReminders()
    }

    private fun loadAppointments() {
        viewModelScope.launch {
            val allAppointments = db.appointmentDao().getAll().map { it.toAppointment() }
            _appointments.value = allAppointments.sortedBy { it.date; it.time }
        }
    }
    private fun loadReminders() {
        viewModelScope.launch {
            val allReminders = db.reminderDao().getAll().map { it.toReminder() }
            _reminders.value = allReminders.sortedBy { it.date }
        }
    }
}
