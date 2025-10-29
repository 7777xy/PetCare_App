package com.example.petcare_app.data

import com.example.petcare_app.ui.screens.Reminder
fun Reminder.toReminderEntity(): ReminderEntity = ReminderEntity(
    id = id,
    title = title,
    date = date,
    completed = completed
)

fun ReminderEntity.toReminder(): Reminder = Reminder(
    id = id,
    title = title,
    date = date,
    completed = completed
)