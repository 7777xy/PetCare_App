package com.example.petcare_app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.*
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.compose.foundation.Image
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.ui.res.painterResource
import com.example.petcare_app.R
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.style.TextAlign
import com.example.petcare_app.viewmodel.HomeViewModel
import com.example.petcare_app.viewmodel.MyPetViewModel
import java.text.SimpleDateFormat
import java.util.Locale


@Composable
fun HomeScreen(navController: NavHostController, viewModel: HomeViewModel, petViewModel: MyPetViewModel) {

    val pets = petViewModel.pets
    val appointments by viewModel.appointments.collectAsState(initial = emptyList())
    val reminders by viewModel.reminders.collectAsState(initial = emptyList())

    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    val nowMs = System.currentTimeMillis()

    val petItems = if (pets.isNotEmpty()) {
        pets.take(2)
        pets.map { pet ->
            "Name: ${pet.basicInfo.name.ifBlank { "Unnamed" }} | " +
                    "Age: ${pet.basicInfo.age.ifBlank { "Unknown" }} | " +
                    "Breed: ${pet.basicInfo.breed.ifBlank { "Unknown" }}"
        }
    } else emptyList()

    val upcomingAppointments = appointments.filter { appointment ->
        try {
            val appointmentTime = sdf.parse("${appointment.date} ${appointment.time}")?.time
            appointmentTime != null && appointmentTime > nowMs
        } catch (e: Exception) {
            false
        }
    }
    val appointmentItems = upcomingAppointments
        .sortedBy { sdf.parse("${it.date} ${it.time}")?.time }
        .take(2)
        .map { "${it.type.capitalize()} Appointment: Find ${it.vetName} in ${it.clinicName} - ${it.date} ${it.time}" }

    val upcomingReminders = reminders.filter { reminder ->
        try {
            val reminderTime = sdf.parse("${reminder.date}")?.time
            reminderTime != null && reminderTime > nowMs
        } catch (e: Exception) {
            false
        }
    }
    val reminderItems = upcomingReminders
        .sortedBy { sdf.parse("${it.date}")?.time }
        .map { "${it.title} - ${it.date}" }

    Surface(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp) // optional spacing between items
        ) {
            // 1. Welcome message
            item {
                Text(
                    text = "Welcome to PetCare! \uD83D\uDC3E",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(20.dp))
            }

            // 2. Description
            item {
                Text(
                    text = "This is a multifunctional and comprehensive APP, helping users to easily monitor their pets’ fitness, receive timely reminders for \n" +
                            "appointments and tasks, and keep organized records of diet and activity.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(18.dp))
            }

            // 3. Profile summary
            item {
                SectionCard(
                    title = "Pet Basic Info",
                    items = petItems,
                    actionLabel = "View Pet Details",
                    onActionClick = {
                        navController.navigate("mypet") {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }

            // 4. Upcoming Appointments Section
            item {
                SectionCard(
                    title = "Upcoming Appointments",
                    items = appointmentItems,
                    actionLabel = "Schedule Appointment",
                    onActionClick = {
                        navController.navigate("appointment") {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }

            // 5. Upcoming Reminders Section
            item {
                SectionCard(
                    title = "Upcoming Reminders",
                    items = reminderItems,
                    actionLabel = "Add Reminder",
                    onActionClick = {
                        navController.navigate("reminder") {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    }
}

/**
 * Reusable section card
 */
@Composable
fun SectionCard(
    title: String,
    items: List<String>,
    actionLabel: String,
    onActionClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
            Text(text = title, style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)

            Spacer(modifier = Modifier.height(12.dp))


            if (items.isNotEmpty()) {

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) { // spacing between items
                    items.take(2).forEach { item ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "•",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(end = 6.dp)
                            )
                            Text(
                                text = item,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp)) // Small space between items
                    }
                }
            }
            else {
                Text(
                    text = "No items to display.", // Placeholder if items list is empty
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onActionClick,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(actionLabel, style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}