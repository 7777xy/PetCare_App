package uiTest

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.petcare_app.ui.screens.Appointment
import com.example.petcare_app.viewmodel.AppointmentViewModel
import com.example.petcare_app.data.AppDatabase
import com.example.petcare_app.viewmodel.ReminderViewModel

class FakeAppointmentViewModel : AppointmentViewModel(db = null) {

    var fakeAppointments = mutableStateOf(listOf<Appointment>())
        private set

    override var upcomingVetAppointments by mutableStateOf(listOf<Appointment>())
    override var pastVetAppointments by mutableStateOf(listOf<Appointment>())
    override var upcomingVaccineAppointments by mutableStateOf(listOf<Appointment>())
    override var pastVaccineAppointments by mutableStateOf(listOf<Appointment>())
    override var completedAppointments by mutableStateOf(listOf<Appointment>())

    init {
        // Initial fake data
        val sample = Appointment(
            id = 1,
            type = "vet",
            vetName = "Dr. Smith",
            clinicName = "Happy Pets Clinic",
            address = "123 Pet Street",
            date = "2025-12-30",
            time = "10:00"
        )
        upcomingVetAppointments = listOf(sample)
        fakeAppointments.value = listOf(sample)
    }

    override fun addAppointment(newAppointment: Appointment) {
        fakeAppointments.value = fakeAppointments.value + newAppointment
        refreshFake()
    }

    override fun updateAppointment(updatedAppointment: Appointment) {
        fakeAppointments.value = fakeAppointments.value.map {
            if (it.id == updatedAppointment.id) updatedAppointment else it
        }
        refreshFake()
    }

    override fun deleteAppointment(appointment: Appointment) {
        fakeAppointments.value = fakeAppointments.value.filter { it.id != appointment.id }
        refreshFake()
    }

    fun refreshFake() {
        val vet = fakeAppointments.value.filter { it.type == "vet" }
        upcomingVetAppointments = vet
        pastVetAppointments = listOf()
        upcomingVaccineAppointments = fakeAppointments.value.filter { it.type == "vaccination" }
        pastVaccineAppointments = listOf()
    }

    override fun markAppointmentCompleted(appointment: Appointment) {
        val updated = appointment.copy(completed = true)
        updateAppointment(updated)
    }
}
