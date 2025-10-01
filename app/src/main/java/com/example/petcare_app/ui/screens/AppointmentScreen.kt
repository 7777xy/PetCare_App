package com.example.petcare_app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

// Data models
data class Appointment(
    val id: Int,
    val type: String, // "vet" or "vaccination"
    val vetName: String,
    val clinicName: String,
    val address: String,
    val date: String,
    val time: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentScreen(navController: NavHostController) {
    var appointments by remember { mutableStateOf(listOf<Appointment>()) }
    var showDialog by remember { mutableStateOf(false) }
    var editAppointment: Appointment? by remember { mutableStateOf(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Appointments",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    editAppointment = null
                    showDialog = true
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Reminder")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            if (appointments.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No appointments yet. Tap + to add one.")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Vet appointments
                    val vetAppointments = appointments.filter { it.type == "vet" }
                    if (vetAppointments.isNotEmpty()) {
                        item {
                            Text(
                                "Vet Appointments",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                        items(vetAppointments, key = { it.id }) { appt ->
                            AppointmentCard(
                                appointment = appt,
                                onEdit = {
                                    editAppointment = it
                                    showDialog = true
                                },
                                onDelete = {
                                    appointments = appointments.filterNot { a -> a.id == it.id }
                                }
                            )
                        }
                    }

                    // Vaccination appointments
                    val vaccineAppointments = appointments.filter { it.type == "vaccination" }
                    if (vaccineAppointments.isNotEmpty()) {
                        item {
                            Text(
                                "Vaccination Appointments",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                        items(vaccineAppointments, key = { it.id }) { appt ->
                            AppointmentCard(
                                appointment = appt,
                                onEdit = {
                                    editAppointment = it
                                    showDialog = true
                                },
                                onDelete = {
                                    appointments = appointments.filterNot { a -> a.id == it.id }
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // Add/Edit dialog
    if (showDialog) {
        AppointmentDialog(
            appointment = editAppointment,
            onDismiss = { showDialog = false },
            onSave = { appt ->
                appointments = if (editAppointment == null) {
                    appointments + appt.copy(id = (appointments.size + 1))
                } else {
                    appointments.map { if (it.id == appt.id) appt else it }
                }
                showDialog = false
            }
        )
    }
}

@Composable
fun AppointmentCard(
    appointment: Appointment,
    onEdit: (Appointment) -> Unit,
    onDelete: (Appointment) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Vet Name: ${appointment.vetName}", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = "Clinic Name: ${appointment.clinicName}", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = "Address: ${appointment.address}", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = "Date: ${appointment.date}", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = "Time: ${appointment.time}", style = MaterialTheme.typography.bodyLarge)

            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Button(onClick = { /* TODO: send message to vet */ }) {
                    Text("Message Vet")
                }
                Row {
                    IconButton(onClick = { onEdit(appointment) }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                    IconButton(onClick = { onDelete(appointment) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                    }
                }
            }
        }
    }
}

@Composable
fun AppointmentDialog(
    appointment: Appointment?,
    onDismiss: () -> Unit,
    onSave: (Appointment) -> Unit
) {
    var vetName by remember { mutableStateOf(appointment?.vetName ?: "") }
    var clinicName by remember { mutableStateOf(appointment?.clinicName ?: "") }
    var address by remember { mutableStateOf(appointment?.address ?: "") }
    var date by remember { mutableStateOf(appointment?.date ?: "") }
    var time by remember { mutableStateOf(appointment?.time ?: "") }
    var type by remember { mutableStateOf(appointment?.type ?: "vet") }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(if (appointment == null) "Add Appointment" else "Edit Appointment") },
        text = {
            Column {
                OutlinedTextField(value = vetName, onValueChange = { vetName = it }, label = { Text("Vet Name") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = clinicName, onValueChange = { clinicName = it }, label = { Text("Clinic Name") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Address") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = date, onValueChange = { date = it }, label = { Text("Date") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = time, onValueChange = { time = it }, label = { Text("Time") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))

                // Appointment type dropdown
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Type: ", modifier = Modifier.padding(end = 8.dp))
                    DropdownMenuBox(
                        selected = type,
                        onSelected = { type = it }
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                if (vetName.isNotBlank() && clinicName.isNotBlank() && address.isNotBlank() && date.isNotBlank() && time.isNotBlank()) {
                    onSave(
                        Appointment(
                            appointment?.id ?: 0,
                            type,
                            vetName,
                            clinicName,
                            address,
                            date,
                            time
                        )
                    )
                }
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = { onDismiss() }) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun DropdownMenuBox(selected: String, onSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val items = listOf("vet", "vaccination")

    Box {
        Button(onClick = { expanded = true }) {
            Text(selected)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item) },
                    onClick = {
                        onSelected(item)
                        expanded = false
                    }
                )
            }
        }
    }
}
