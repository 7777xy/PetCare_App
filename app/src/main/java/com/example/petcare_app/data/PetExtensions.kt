package com.example.petcare_app.data

import com.example.petcare_app.ui.screens.Pet
import com.example.petcare_app.ui.screens.BasicInfo
import com.example.petcare_app.ui.screens.Lifestyle
import com.example.petcare_app.ui.screens.History

fun Pet.toPetEntity(): PetEntity = PetEntity(
    id = id,
    name = basicInfo.name,
    age = basicInfo.age,
    species = basicInfo.species,
    weight = basicInfo.weight,
    gender = basicInfo.gender,
    breed = basicInfo.breed,
    color = basicInfo.color,
    exerciseRoutine = lifestyle.exerciseRoutine,
    diet = lifestyle.diet,
    medicalHistory = history.medicalHistory,
    vaccinationHistory = history.vaccinationHistory
)

fun PetEntity.toPet(): Pet = Pet(
    id = id,
    basicInfo = BasicInfo(name, age, species, weight, gender, breed, color),
    lifestyle = Lifestyle(exerciseRoutine, diet),
    history = History(medicalHistory, vaccinationHistory)
)
