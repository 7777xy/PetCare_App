package com.example.petcare_app.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import com.example.petcare_app.viewmodel.ReminderViewModel
import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.clickable
import androidx.compose.ui.platform.LocalContext
import java.util.Calendar
import android.app.TimePickerDialog
import android.widget.TimePicker



data class Reminder(
    val id: Int,
    var title: String,
    var date: String
)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderScreen(navController: NavHostController, viewModel: ReminderViewModel) {
    val reminders = viewModel.reminders
    var showDialog by remember { mutableStateOf(false) }
    var editReminder: Reminder? by remember { mutableStateOf(null) }

    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text(
                    text = "Reminders",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            })
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
            if (reminders.isEmpty()) {
                item {Box(
                    modifier = Modifier
                        .fillParentMaxSize() // Make the Box take the whole LazyColumn space
                        .padding(16.dp),
                    contentAlignment = Alignment.Center // Center content horizontally and vertically
                ) {
                    Text("No reminders yet. Tap + to add one.")
                }}
        } else {
                items(reminders, key = { it.id }) { reminder ->
                    ReminderItem(
                        reminder = reminder,
                        onEdit = {
                            editReminder = it
                            showDialog = true
                        },
                        onDelete = {
                            viewModel.deleteReminder(it)
                        }
                    )
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
                showDialog = false
            }
        )
    }
}

@Composable
fun ReminderItem(
    reminder: Reminder,
    onEdit: (Reminder) -> Unit,
    onDelete: (Reminder) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = reminder.title, style = MaterialTheme.typography.titleLarge, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = reminder.date, style = MaterialTheme.typography.bodyLarge)
            }
            Row {
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
    var dateText by remember { mutableStateOf(reminder?.date ?: "") }
    val datePicker = remember {
        DatePickerDialog(
            context,
            { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                calendar.set(year, month, dayOfMonth)
                dateText = "${year}-${month + 1}-${dayOfMonth}" // yyyy-MM-dd
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

// --- Time Picker ---
    var timeText by remember { mutableStateOf(reminder?.date?.substringAfter(" ", "") ?: "") } // store time separately
    val timePicker = remember {
        TimePickerDialog(
            context,
            { _: TimePicker, hour: Int, minute: Int ->
                timeText = String.format("%02d:%02d", hour, minute) // HH:mm
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true // 24-hour format
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
                    onValueChange = {}, // read-only
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
                if (title.isNotBlank() && dateText.isNotBlank()) {
                    val combinedDateTime = if (timeText.isNotBlank()) "$dateText $timeText" else dateText
                    onSave(Reminder(reminder?.id ?: 0, title, combinedDateTime))
                }
            }) { Text("Save") }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("Cancel") }
        }
    )}

