package com.example.petcare_app.data

import com.example.petcare_app.ui.screens.Appointment
fun Appointment.toAppointmentEntity(): AppointmentEntity = AppointmentEntity(
    id = id,
    type = type,
    vetName = vetName,
    clinicName = clinicName,
    address = address,
    date = date,
    time = time,
    completed = completed
)

fun AppointmentEntity.toAppointment(): Appointment = Appointment(
    id = id,
    type = type,
    vetName = vetName,
    clinicName = clinicName,
    address = address,
    date = date,
    time = time,
    completed = completed
)