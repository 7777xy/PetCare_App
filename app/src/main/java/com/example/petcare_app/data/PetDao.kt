package com.example.petcare_app.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface PetDao {
    @Query("SELECT * FROM PetEntity")
    suspend fun getAll(): List<PetEntity>

    @Insert
    suspend fun insert(pet: PetEntity)

    @Update
    suspend fun update(pet: PetEntity)

    @Delete
    suspend fun delete(pet: PetEntity)
}