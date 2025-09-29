package com.example.petcare_app.ui.navigation

import androidx.annotation.DrawableRes
import com.example.petcare_app.R

sealed class BottomNavItem(val route: String, val label: String, @DrawableRes val iconRes: Int) {
    object Home : BottomNavItem("home", "Home", R.drawable.ic_home)
    object MyPet : BottomNavItem("mypet", "My Pets", R.drawable.ic_my_pet)
    object Appointment : BottomNavItem("appointment", "Appointments", R.drawable.ic_appointment)
    object Reminder : BottomNavItem("reminder", "Reminders", R.drawable.ic_reminder)
}