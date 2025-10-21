package com.example.petcare_app.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [PetEntity::class, AppointmentEntity::class, ReminderEntity::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun petDao(): PetDao
    abstract fun appointmentDao(): AppointmentDao
    abstract fun reminderDao(): ReminderDao
}
