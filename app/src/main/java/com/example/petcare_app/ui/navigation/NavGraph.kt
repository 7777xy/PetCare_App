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
import com.example.petcare_app.ui.screens.ReminderHistoryScreen
import com.example.petcare_app.ui.screens.AppointmentHistoryScreen
import com.example.petcare_app.viewmodel.MyPetViewModel
import com.example.petcare_app.viewmodel.AppointmentViewModel
import com.example.petcare_app.viewmodel.ReminderViewModel

@Composable
fun NavHostContainer(navController: NavHostController, modifier: Modifier = Modifier, myPetViewModel: MyPetViewModel, appointmentViewModel: AppointmentViewModel, reminderViewModel: ReminderViewModel) {
    NavHost(navController = navController, startDestination = BottomNavItem.Home.route, modifier = modifier) {
        composable(BottomNavItem.Home.route) { HomeScreen(navController = navController) }
        composable(BottomNavItem.MyPet.route) { MyPetScreen(navController = navController, viewModel = myPetViewModel) }
        composable(BottomNavItem.Appointment.route) { AppointmentScreen(navController = navController, viewModel = appointmentViewModel) }
        composable(BottomNavItem.Reminder.route) { ReminderScreen(navController = navController, viewModel = reminderViewModel) }
        composable("reminder_history") { ReminderHistoryScreen(navController = navController, viewModel = reminderViewModel) }
        composable("appointment_history") { AppointmentHistoryScreen(navController = navController, viewModel = appointmentViewModel) }
    }
}
