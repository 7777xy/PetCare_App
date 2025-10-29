package com.example.petcare_app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AppointmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String,
    val vetName: String,
    val clinicName: String,
    val address: String,
    val date: String,
    val time: String,
    val completed: Boolean = false
)