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

open class HomeViewModel(private val db: AppDatabase? = null) : ViewModel() {

    private val _pets = MutableStateFlow<List<Appointment>>(emptyList())
    val pets = _pets.asStateFlow()

    private val _appointments = MutableStateFlow<List<Appointment>>(emptyList())
    open val appointments = _appointments.asStateFlow()

    private val _reminders = MutableStateFlow<List<Reminder>>(emptyList())
    open val reminders = _reminders.asStateFlow()

    init {
        // Only load data if database is provided (null-safe for fake ViewModels)
        if (db != null) {
            loadAppointments()
            loadReminders()
        }
    }

    private fun loadAppointments() {
        viewModelScope.launch {
            db?.appointmentDao()?.getAll()?.map { it.toAppointment() }?.let { allAppointments ->
                _appointments.value = allAppointments.sortedBy { it.date; it.time }
            }
        }
    }

    private fun loadReminders() {
        viewModelScope.launch {
            db?.reminderDao()?.getAll()?.map { it.toReminder() }?.let { allReminders ->
                _reminders.value = allReminders.sortedBy { it.date }
            }
        }
    }
}
