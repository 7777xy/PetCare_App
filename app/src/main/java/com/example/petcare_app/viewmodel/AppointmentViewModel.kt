package com.example.petcare_app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

import com.example.petcare_app.data.AppDatabase
import com.example.petcare_app.data.toAppointmentEntity
import com.example.petcare_app.data.toAppointment
import com.example.petcare_app.ui.screens.Appointment

class AppointmentViewModel(private val db: AppDatabase) : ViewModel() {

    // Holds the current list of appointments as observable state.
    var appointments by mutableStateOf(listOf<Appointment>())
        private set

    init {
        // Fetch all appointments from the database when the ViewModel is created.
        viewModelScope.launch {
            // Assumes you have a toAppointment() extension function
            appointments = db.appointmentDao().getAll().map { it.toAppointment() }
        }
    }

    /**
     * Adds a new, empty appointment to the database and updates the local state.
     */
    fun addAppointment(newAppointment: Appointment) {
        viewModelScope.launch {
            // Assumes you have a toAppointmentEntity() extension function
            db.appointmentDao().insert(newAppointment.toAppointmentEntity())
            // Refresh the list from the database
            appointments = db.appointmentDao().getAll().map { it.toAppointment() }
        }
    }
    /**
     * Updates an existing appointment in the database.
     * @param updatedAppointment The appointment object with updated information.
     */
    fun updateAppointment(updatedAppointment: Appointment) {
        viewModelScope.launch {
            db.appointmentDao().update(updatedAppointment.toAppointmentEntity())
            appointments = db.appointmentDao().getAll().map { it.toAppointment() }
        }
    }

    /**
     * Deletes a specific appointment from the database.
     * @param appointment The appointment to be deleted.
     */
    fun deleteAppointment(appointment: Appointment) {
        viewModelScope.launch {
            db.appointmentDao().delete(appointment.toAppointmentEntity())
            appointments = db.appointmentDao().getAll().map { it.toAppointment() }
        }
    }
}
