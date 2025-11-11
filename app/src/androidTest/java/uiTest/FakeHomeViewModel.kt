package uiTest

import com.example.petcare_app.data.AppDatabase
import com.example.petcare_app.ui.screens.Appointment
import com.example.petcare_app.ui.screens.Reminder
import com.example.petcare_app.viewmodel.HomeViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeHomeViewModel : HomeViewModel(db = null) {

    private val _appointments = MutableStateFlow<List<Appointment>>(emptyList())
    override val appointments: StateFlow<List<Appointment>> = _appointments.asStateFlow()

    private val _reminders = MutableStateFlow<List<Reminder>>(emptyList())
    override val reminders: StateFlow<List<Reminder>> = _reminders.asStateFlow()

    init {
        // Add fake data
        val sampleAppointment = Appointment(
            id = 1,
            type = "vet",
            vetName = "Dr. Smith",
            clinicName = "Happy Pets Clinic",
            address = "123 Pet Street",
            date = "2025-12-30",
            time = "10:00"
        )
        val sampleReminder = Reminder(
            id = 1,
            title = "Vet Checkup",
            date = "2025-12-31 10:00",
            completed = false
        )

        _appointments.value = listOf(sampleAppointment)
        _reminders.value = listOf(sampleReminder)
    }
}
