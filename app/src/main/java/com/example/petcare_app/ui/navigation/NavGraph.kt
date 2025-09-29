package com.example.petcare_app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.petcare_app.ui.screens.AppointmentScreen
import com.example.petcare_app.ui.screens.HomeScreen
import com.example.petcare_app.ui.screens.MyPetScreen
import com.example.petcare_app.ui.screens.ReminderScreen

@Composable
fun NavHostContainer(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(navController = navController, startDestination = BottomNavItem.Home.route, modifier = modifier) {
        composable(BottomNavItem.Home.route) { HomeScreen() }
        composable(BottomNavItem.MyPet.route) { MyPetScreen() }
        composable(BottomNavItem.Appointment.route) { AppointmentScreen() }
        composable(BottomNavItem.Reminder.route) { ReminderScreen() }
    }
}
