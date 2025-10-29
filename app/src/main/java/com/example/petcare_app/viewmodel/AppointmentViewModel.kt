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
import com.example.petcare_app.data.toAppointmentEntity
import com.example.petcare_app.data.toAppointment
import com.example.petcare_app.ui.screens.Appointment
import java.text.SimpleDateFormat
import java.util.*

class AppointmentViewModel(private val db: AppDatabase) : ViewModel() {

    // Vet
    var upcomingVetAppointments by mutableStateOf(listOf<Appointment>())
        private set
    var pastVetAppointments by mutableStateOf(listOf<Appointment>())
        private set

    // Vaccine
    var upcomingVaccineAppointments by mutableStateOf(listOf<Appointment>())
        private set
    var pastVaccineAppointments by mutableStateOf(listOf<Appointment>())
        private set

    // History
    var completedAppointments by mutableStateOf(listOf<Appointment>())
        private set

    init {
        viewModelScope.launch { refreshAppointments() }
    }

    fun addAppointment(newAppointment: Appointment) {
        viewModelScope.launch {
            db.appointmentDao().insert(newAppointment.toAppointmentEntity())
            refreshAppointments()
        }
    }

    fun updateAppointment(updatedAppointment: Appointment) {
        viewModelScope.launch {
            db.appointmentDao().update(updatedAppointment.toAppointmentEntity())
            refreshAppointments()
        }
    }

    fun deleteAppointment(appointment: Appointment) {
        viewModelScope.launch {
            db.appointmentDao().delete(appointment.toAppointmentEntity())
            refreshAppointments()
        }
    }

    private suspend fun refreshAppointments() {
        val all = db.appointmentDao().getAll().map { it.toAppointment() }
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        sdf.timeZone = TimeZone.getDefault()
        val nowMs = System.currentTimeMillis()

        val upcomingVet = mutableListOf<Appointment>()
        val pastVet = mutableListOf<Appointment>()
        val upcomingVaccine = mutableListOf<Appointment>()
        val pastVaccine = mutableListOf<Appointment>()
        val completed = mutableListOf<Appointment>()

        for (appointment in all) {
            if (appointment.completed) {
                completed.add(appointment)
                continue
            }

            val time = try {
                sdf.parse("${appointment.date} ${appointment.time}")?.time
            } catch (_: Exception) {
                null
            }

            val isPast = time != null && time < nowMs

            when (appointment.type.lowercase()) {
                "vet" -> {
                    if (isPast) pastVet.add(appointment)
                    else upcomingVet.add(appointment)
                }
                "vaccination" -> {
                    if (isPast) pastVaccine.add(appointment)
                    else upcomingVaccine.add(appointment)
                }
            }
        }

        // Update state on Main dispatcher and create new list instances to ensure recomposition
        withContext(Dispatchers.Main) {
            upcomingVetAppointments = upcomingVet.sortedBy { it.date }.toList()
            pastVetAppointments = pastVet.sortedBy { it.date }.toList()
            upcomingVaccineAppointments = upcomingVaccine.sortedBy { it.date }.toList()
            pastVaccineAppointments = pastVaccine.sortedBy { it.date }.toList()
            completedAppointments = completed.sortedBy { it.date }.toList()
        }
    }

    fun markAppointmentCompleted(appointment: Appointment) {
        android.util.Log.d("AppointmentViewModel", "markAppointmentCompleted called: ${appointment.vetName}, completed: ${appointment.completed}")
        viewModelScope.launch {
            db.appointmentDao().update(appointment.toAppointmentEntity())
            android.util.Log.d("AppointmentViewModel", "Appointment updated in DB, refreshing list...")
            refreshAppointments()
            android.util.Log.d("AppointmentViewModel", "List refreshed.")
        }
    }


}
