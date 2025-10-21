package com.example.petcare_app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

import com.example.petcare_app.data.AppDatabase
import com.example.petcare_app.data.toPetEntity
import com.example.petcare_app.data.toPet
import com.example.petcare_app.ui.screens.Pet
import com.example.petcare_app.ui.screens.BasicInfo
import com.example.petcare_app.ui.screens.Lifestyle
import com.example.petcare_app.ui.screens.History

class MyPetViewModel(private val db: AppDatabase) : ViewModel() {

    var pets by mutableStateOf(listOf<Pet>())
        private set

    init {
        viewModelScope.launch {
            pets = db.petDao().getAll().map { it.toPet() }
        }
    }

    fun addPet(newPet: Pet) {
        viewModelScope.launch {
            db.petDao().insert(newPet.toPetEntity())
            pets = db.petDao().getAll().map { it.toPet() }
        }
    }

    fun updatePet(updatedPet: Pet) {
        viewModelScope.launch {
            db.petDao().update(updatedPet.toPetEntity())
            pets = db.petDao().getAll().map { it.toPet() }
        }
    }

    fun deletePet(pet: Pet) {
        viewModelScope.launch {
            db.petDao().delete(pet.toPetEntity())
            pets = db.petDao().getAll().map { it.toPet() }
        }
    }
}


