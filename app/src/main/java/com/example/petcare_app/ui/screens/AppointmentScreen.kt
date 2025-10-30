package com.example.petcare_app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.petcare_app.viewmodel.AppointmentViewModel
import java.util.*
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.compose.material.icons.filled.Phone
import com.example.petcare_app.data.ReminderEntity
import com.example.petcare_app.viewmodel.ReminderViewModel
import kotlin.random.Random

data class Appointment(
    val id: Int,
    val type: String, // "vet" or "vaccination"
    val vetName: String,
    val clinicName: String,
    val address: String,
    val date: String,
    val time: String,
    val completed: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentScreen(navController: NavHostController, viewModel: AppointmentViewModel, reminderViewModel: ReminderViewModel) {
    val upcomingVet = viewModel.upcomingVetAppointments
    val pastVet = viewModel.pastVetAppointments
    val upcomingVaccine = viewModel.upcomingVaccineAppointments
    val pastVaccine = viewModel.pastVaccineAppointments
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
                },
                actions = {
                    TextButton(onClick = { navController.navigate("appointment_history") }) {
                        Text("History")
                    }
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
                Icon(Icons.Default.Add, contentDescription = "Add Appointment")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (upcomingVet.isEmpty() && pastVet.isEmpty() &&
                upcomingVaccine.isEmpty() && pastVaccine.isEmpty()
            ) {
                item {
                    Box(
                        modifier = Modifier.fillParentMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No appointments yet. Tap + to add one.")
                    }
                }
            } else {
                // --- Vet Appointments ---
                if (upcomingVet.isNotEmpty()) {
                    item { SectionTitle("Upcoming Vet Appointments") }
                    items(upcomingVet, key = { it.id }) { appt ->
                        AppointmentCard(
                            appointment = appt,
                            navController = navController,
                            reminderViewModel = reminderViewModel,
                            onEdit = { editAppointment = it; showDialog = true },
                            onDelete = { viewModel.deleteAppointment(it) },
                            onCompletedChange = { checked ->
                                viewModel.markAppointmentCompleted(appt.copy(completed = checked))
                            }
                        )
                    }
                }

                if (pastVet.isNotEmpty()) {
                    item { SectionTitle("Past Vet Appointments") }
                    items(pastVet, key = { it.id }) { appt ->
                        AppointmentCard(
                            appointment = appt,
                            navController = navController,
                            reminderViewModel = reminderViewModel,
                            onEdit = { editAppointment = it; showDialog = true },
                            onDelete = { viewModel.deleteAppointment(it) },
                            onCompletedChange = { checked ->
                                viewModel.markAppointmentCompleted(appt.copy(completed = checked))
                            }
                        )
                    }
                }

                // --- Vaccine Appointments ---
                if (upcomingVaccine.isNotEmpty()) {
                    item { SectionTitle("Upcoming Vaccination Appointments") }
                    items(upcomingVaccine, key = { it.id }) { appt ->
                        AppointmentCard(
                            appointment = appt,
                            navController = navController,
                            reminderViewModel = reminderViewModel,
                            onEdit = { editAppointment = it; showDialog = true },
                            onDelete = { viewModel.deleteAppointment(it) },
                            onCompletedChange = { checked ->
                                viewModel.markAppointmentCompleted(appt.copy(completed = checked))
                            }
                        )
                    }
                }

                if (pastVaccine.isNotEmpty()) {
                    item { SectionTitle("Past Vaccination Appointments") }
                    items(pastVaccine, key = { it.id }) { appt ->
                        AppointmentCard(
                            appointment = appt,
                            navController = navController,
                            reminderViewModel = reminderViewModel,
                            onEdit = { editAppointment = it; showDialog = true },
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

    if (showDialog) {
        AppointmentDialog(
            appointment = editAppointment,
            onDismiss = { showDialog = false },
            onSave = { appt ->
                if (editAppointment == null) {
                    viewModel.addAppointment(appt)
                } else {
                    viewModel.updateAppointment(appt)
                }
                showDialog = false
            }
        )
    }
}

@Composable
fun SectionTitle(title: String) {
    Column {
        Text(
            title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}


@Composable
fun AppointmentCard(
    appointment: Appointment,
    navController: NavHostController,
    reminderViewModel: ReminderViewModel,
    onEdit: (Appointment) -> Unit,
    onDelete: (Appointment) -> Unit,
    onCompletedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Vet: ${appointment.vetName}",
                style = MaterialTheme.typography.titleMedium,
                textDecoration = if (appointment.completed) TextDecoration.LineThrough else TextDecoration.None)
            Text(text = "Clinic: ${appointment.clinicName}",
                style = MaterialTheme.typography.titleMedium,
                textDecoration = if (appointment.completed) TextDecoration.LineThrough else TextDecoration.None)
            Text(text ="Address: ${appointment.address}",
                style = MaterialTheme.typography.titleMedium,
                textDecoration = if (appointment.completed) TextDecoration.LineThrough else TextDecoration.None)
            Text(text = "Date: ${appointment.date}",
                style = MaterialTheme.typography.titleMedium,
                textDecoration = if (appointment.completed) TextDecoration.LineThrough else TextDecoration.None)
            Text(text = "Time: ${appointment.time}",
                style = MaterialTheme.typography.titleMedium,
                textDecoration = if (appointment.completed) TextDecoration.LineThrough else TextDecoration.None)

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) { Button(onClick = {
                val reminderDateTime = "${appointment.date} ${appointment.time}"  // combine date + time

                val newReminder = ReminderEntity(
                    title = "Go to ${appointment.clinicName} to find ${appointment.vetName}",
                    date = reminderDateTime,  // you can reuse appointment date/time
                    completed = false
                )

                reminderViewModel.insertReminder(newReminder)

                navController.navigate("reminder") {
                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }) {
                Text("Add Reminder")
            }
                Row {
                    Checkbox(
                        checked = appointment.completed,
                        onCheckedChange = onCompletedChange
                    )
                    IconButton(onClick = { /* TODO: No action yet, just visual */ }) {
                        Icon(Icons.Default.Phone, contentDescription = "Contact Staff")
                    }
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
    var type by remember { mutableStateOf(appointment?.type ?: "vet") }

    val context = LocalContext.current
    val calendar = remember { Calendar.getInstance() }

    var dateText by remember { mutableStateOf(appointment?.date ?: "") }
    val datePicker = remember {
        DatePickerDialog(
            context,
            { _: DatePicker, year: Int, month: Int, day: Int ->
                calendar.set(year, month, day)
                dateText = String.format("%04d-%02d-%02d", year, month + 1, day)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    var timeText by remember { mutableStateOf(appointment?.time ?: "") }
    val timePicker = remember {
        TimePickerDialog(
            context,
            { _: TimePicker, hour: Int, minute: Int ->
                timeText = String.format("%02d:%02d", hour, minute)
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (appointment == null) "Add Appointment" else "Edit Appointment") },
        text = {
            Column {
                OutlinedTextField(value = vetName, onValueChange = { vetName = it }, label = { Text("Vet Name") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = clinicName, onValueChange = { clinicName = it }, label = { Text("Clinic Name") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Address") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = dateText,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Date") },
                    modifier = Modifier.fillMaxWidth().clickable { datePicker.show() }
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = timeText,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Time") },
                    modifier = Modifier.fillMaxWidth().clickable { timePicker.show() }
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Type: ", modifier = Modifier.padding(end = 8.dp))
                    DropdownMenuBox(selected = type, onSelected = { type = it })
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                if (vetName.isNotBlank() && clinicName.isNotBlank() && address.isNotBlank() && dateText.isNotBlank() && timeText.isNotBlank()) {
                    onSave(
                        Appointment(
                            appointment?.id ?: Random.nextInt(1_000_000),
                            type,
                            vetName,
                            clinicName,
                            address,
                            dateText,
                            timeText,
                            appointment?.completed ?: false
                        )
                    )
                }
            }) { Text("Save") }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun DropdownMenuBox(selected: String, onSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val items = listOf("vet", "vaccination")
    Box {
        Button(onClick = { expanded = true }) { Text(selected) }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            items.forEach { item ->
                DropdownMenuItem(text = { Text(item) }, onClick = {
                    onSelected(item)
                    expanded = false
                })
            }
        }
    }
}
