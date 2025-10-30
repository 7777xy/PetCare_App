package com.example.petcare_app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.petcare_app.viewmodel.AppointmentViewModel
import com.example.petcare_app.viewmodel.ReminderViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentHistoryScreen(navController: NavHostController, viewModel: AppointmentViewModel, reminderViewModel: ReminderViewModel) {
    val history = viewModel.completedAppointments

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Appointment History") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            if (history.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillParentMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No completed appointments yet.")
                    }
                }
            } else {
                items(history, key = { it.id }) { appt ->
                    AppointmentCard(
                        appointment = appt,
                        navController = navController,
                        reminderViewModel = reminderViewModel,
                        onEdit = { /* optional edit in history */ },
                        onDelete = { viewModel.deleteAppointment(it) },
                        onCompletedChange = { checked ->
                            viewModel.markAppointmentCompleted(appt.copy(completed = checked))
                        }
                    )
                }
            }
        }
    }
}
