package com.example.petcare_app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PetEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String = "",
    val age: String = "",
    val species: String = "",
    val weight: String = "",
    val gender: String = "",
    val breed: String = "",
    val color: String = "",
    val exerciseRoutine: String = "",
    val diet: String = "",
    val medicalHistory: String = "",
    val vaccinationHistory: String = ""
)