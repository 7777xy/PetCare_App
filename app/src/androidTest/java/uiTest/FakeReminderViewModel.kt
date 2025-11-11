package uiTest

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.petcare_app.ui.screens.Reminder
import com.example.petcare_app.viewmodel.ReminderViewModel
import com.example.petcare_app.data.AppDatabase

class FakeReminderViewModel: ReminderViewModel(db = null) {

    var fakeReminders = mutableStateOf(listOf<Reminder>())
        private set

    override var upcomingReminders by mutableStateOf(listOf<Reminder>())
    override var pastReminders by mutableStateOf(listOf<Reminder>())
    override var completedReminders by mutableStateOf(listOf<Reminder>())

    init {
        // Initial fake data
        val sampleUpcoming = Reminder(
            id = 1,
            title = "Vet Checkup",
            date = "2025-12-31 10:00",
            completed = false
        )
        val samplePast = Reminder(
            id = 2,
            title = "Past Reminder",
            date = "2025-12-30 09:00",
            completed = false
        )
        upcomingReminders = listOf(sampleUpcoming)
        pastReminders = listOf(samplePast)
        fakeReminders.value = listOf(sampleUpcoming, samplePast)
    }

    override fun addReminder(newReminder: Reminder) {
        fakeReminders.value = fakeReminders.value + newReminder
        refreshFake()
    }

    override fun updateReminder(updatedReminder: Reminder) {
        fakeReminders.value = fakeReminders.value.map {
            if (it.id == updatedReminder.id) updatedReminder else it
        }
        refreshFake()
    }

    override fun deleteReminder(reminder: Reminder) {
        fakeReminders.value = fakeReminders.value.filter { it.id != reminder.id }
        refreshFake()
    }

    private fun refreshFake() {
        val nowMs = System.currentTimeMillis()
        upcomingReminders = fakeReminders.value.filter {
            !it.completed && parseDateTime(it.date) >= nowMs
        }
        pastReminders = fakeReminders.value.filter {
            !it.completed && parseDateTime(it.date) < nowMs
        }
        completedReminders = fakeReminders.value.filter { it.completed }
    }

    override fun markReminderCompleted(reminder: Reminder) {
        val updated = reminder.copy(completed = true)
        updateReminder(updated)
    }

    private fun parseDateTime(dateTime: String): Long {
        return try {
            val sdf = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault())
            sdf.timeZone = java.util.TimeZone.getDefault()
            sdf.parse(dateTime)?.time ?: 0L
        } catch (_: Exception) {
            0L
        }
    }
}

