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
import androidx.navigation.NavHostController
import com.example.petcare_app.viewmodel.ReminderViewModel
import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.clickable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import java.util.Calendar
import android.app.TimePickerDialog
import android.widget.TimePicker
import java.util.Random


data class Reminder(
    val id: Int,
    var title: String,
    var date: String,
    var completed: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderScreen(navController: NavHostController, viewModel: ReminderViewModel) {
    // Observe state directly - since viewModel properties use mutableStateOf, 
    // accessing them triggers recomposition automatically
    val upcomingReminders = viewModel.upcomingReminders
    val pastReminders = viewModel.pastReminders
    var showDialog by remember { mutableStateOf(false) }
    var editReminder: Reminder? by remember { mutableStateOf(null) }

    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Reminders",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                actions = {
                    TextButton(onClick = { navController.navigate("reminder_history") }) {
                        Text("History")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    editReminder = null
                    showDialog = true
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Reminder")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 16.dp)

        ){
            if (upcomingReminders.isEmpty() && pastReminders.isEmpty()) {
                item {Box(
                    modifier = Modifier
                        .fillParentMaxSize() // Make the Box take the whole LazyColumn space
                        .padding(16.dp),
                    contentAlignment = Alignment.Center // Center content horizontally and vertically
                ) {
                    Text("No reminders yet. Tap + to add one.")
                }}
            } else {
                if (upcomingReminders.isNotEmpty()) {
                    item {
                        Column {
                            Text(
                                text = "Upcoming",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                    items(upcomingReminders, key = { it.id }) { reminder ->
                        ReminderItem(
                            reminder = reminder,
                            onEdit = {
                                editReminder = it
                                showDialog = true
                            },
                            onDelete = {
                                // Cancel any scheduled alarm and then delete
                                viewModel.deleteReminderAndCancel(context, it)
                                android.util.Log.d("ReminderScreen", "Reminder deleted and alarm canceled: ${it.title}")
                            },
                            onCompletedChange = { checked ->
                                viewModel.markReminderCompleted(reminder.copy(completed = checked))
                            }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }

                if (pastReminders.isNotEmpty()) {
                    item {
                        Column {
                            Text(
                                text = "Past",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                    items(pastReminders, key = { it.id }) { reminder ->
                        ReminderItem(
                            reminder = reminder,
                            onEdit = {
                                editReminder = it
                                showDialog = true
                            },
                            onDelete = {
                                // Cancel any scheduled alarm and then delete
                                viewModel.deleteReminderAndCancel(context, it)
                                android.util.Log.d("ReminderScreen", "Reminder deleted and alarm canceled: ${it.title}")
                            },
                            onCompletedChange = { checked ->
                                viewModel.markReminderCompleted(reminder.copy(completed = checked))
                            }
                        )
                    }
                }
            }
        }
    }

    // Add/Edit dialog
    if (showDialog) {
        ReminderDialog(
            reminder = editReminder,
            onDismiss = { showDialog = false },
            onSave = { reminder ->
                if (editReminder == null) {
                    viewModel.addReminder(reminder)
                } else {
                    viewModel.updateReminder(reminder)
                }
                // Schedule notification here
                viewModel.scheduleReminder(context, reminder)
                android.util.Log.d("ReminderScreen", "Reminder scheduled: ${reminder.title}")

                showDialog = false
            }
        )
    }
}

@Composable
fun ReminderItem(
    reminder: Reminder,
    onEdit: (Reminder) -> Unit,
    onDelete: (Reminder) -> Unit,
    onCompletedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = reminder.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    textDecoration = if (reminder.completed) TextDecoration.LineThrough else TextDecoration.None
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = reminder.date,
                    style = MaterialTheme.typography.bodyLarge,
                    textDecoration = if (reminder.completed) TextDecoration.LineThrough else TextDecoration.None
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = reminder.completed,
                    onCheckedChange = { onCompletedChange(it) }
                )
                IconButton(onClick = { onEdit(reminder) }) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                }
                IconButton(onClick = { onDelete(reminder) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                }
            }
        }
    }
}

@Composable
fun ReminderDialog(
    reminder: Reminder?,
    onDismiss: () -> Unit,
    onSave: (Reminder) -> Unit
) {
    var title by remember { mutableStateOf(reminder?.title ?: "") }

    val context = LocalContext.current
    val calendar = remember { Calendar.getInstance() }

    // --- Date Picker ---
    var dateText by remember { mutableStateOf(reminder?.date?.substringBefore(" ", "") ?: "") }
    val datePicker = remember {
        DatePickerDialog(
            context,
            { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                calendar.set(year, month, dayOfMonth)
                dateText = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    // --- Time Picker ---
    var timeText by remember { mutableStateOf(reminder?.date?.substringAfter(" ", "") ?: "") }
    val timePicker = remember {
        TimePickerDialog(
            context,
            { _: TimePicker, hour: Int, minute: Int ->
                // 24-hour format enforced
                timeText = String.format("%02d:%02d", hour, minute)
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (reminder == null) "Add Reminder" else "Edit Reminder") },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = dateText,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Date") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { datePicker.show() }
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = timeText,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Time") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { timePicker.show() }
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                if (title.isNotBlank() && dateText.isNotBlank() && timeText.isNotBlank()) {
                    val combinedDateTime = "$dateText $timeText"
                    onSave(Reminder(reminder?.id ?: Random().nextInt(1_000_000), title, combinedDateTime, reminder?.completed ?: false))
                }
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}


