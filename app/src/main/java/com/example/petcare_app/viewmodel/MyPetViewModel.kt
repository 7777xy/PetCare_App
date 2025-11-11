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

open class MyPetViewModel(private val db: AppDatabase? = null) : ViewModel() {

    open var pets by mutableStateOf(listOf<Pet>())
        private set

    init {
        // Only load from DB if db is not null
        db?.let { database ->
            viewModelScope.launch {
                pets = database.petDao().getAll().map { it.toPet() }
            }
        }
    }

    open fun addPet(newPet: Pet) {
        if (db != null) {
            viewModelScope.launch {
                db.petDao().insert(newPet.toPetEntity())
                pets = db.petDao().getAll().map { it.toPet() }
            }
        }
    }

    open fun updatePet(updatedPet: Pet) {
        if (db != null) {
            viewModelScope.launch {
                db.petDao().update(updatedPet.toPetEntity())
                pets = db.petDao().getAll().map { it.toPet() }
            }
        }
    }

    open fun deletePet(pet: Pet) {
        if (db != null) {
            viewModelScope.launch {
                db.petDao().delete(pet.toPetEntity())
                pets = db.petDao().getAll().map { it.toPet() }
            }
        }
    }

    open fun setPetsForTest(testPets: List<Pet>) {
        pets = testPets
    }
}


