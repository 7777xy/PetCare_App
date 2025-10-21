package com.example.petcare_app.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface AppointmentDao {
    @Query("SELECT * FROM AppointmentEntity")
    suspend fun getAll(): List<AppointmentEntity>

    @Insert
    suspend fun insert(appt: AppointmentEntity)

    @Update
    suspend fun update(appt: AppointmentEntity)

    @Delete
    suspend fun delete(appt: AppointmentEntity)
}