package integrationTest

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.example.petcare_app.data.AppDatabase
import com.example.petcare_app.ui.screens.Appointment
import com.example.petcare_app.viewmodel.AppointmentViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AppointmentIntegrationTest {

    private lateinit var database: AppDatabase
    private lateinit var viewModel: AppointmentViewModel

    @Before
    fun setup() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries() // Only for testing
            .build()
        viewModel = AppointmentViewModel(database)
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun testAddAppointment() = runBlocking {
        val appt = Appointment(
            id = 1,
            type = "vet",
            vetName = "Dr. Lee",
            clinicName = "Happy Pets",
            address = "123 Pet Street",
            date = "2025-12-31", // Only when the date before this date, the upcomingVetAppointment will have value
            time = "10:00",
            completed = false
        )

        viewModel.addAppointment(appt)

        // Wait a little for ViewModel to refresh LiveData/StateFlow
        kotlinx.coroutines.delay(100)

        assertEquals(1, viewModel.upcomingVetAppointments.size)
        assertEquals("Dr. Lee", viewModel.upcomingVetAppointments[0].vetName)

    }

    @Test
    fun testMarkCompleted() = runBlocking {
        val appt = Appointment(
            id = 1,
            type = "vet",
            vetName = "Dr. Lee",
            clinicName = "Happy Pets",
            address = "123 Pet Street",
            date = "2025-12-31",
            time = "10:00",
            completed = false
        )

        viewModel.addAppointment(appt)

        // mark completed
        val completedAppt = appt.copy(completed = true)
        viewModel.markAppointmentCompleted(completedAppt)

        // wait for LiveData/StateFlow to update
        kotlinx.coroutines.delay(100)

        // assert
        assertTrue(viewModel.upcomingVetAppointments.isEmpty())
        assertEquals(1, viewModel.completedAppointments.size)
        assertTrue(viewModel.completedAppointments[0].completed)
    }


    @Test
    fun testUpdateAppointment() = runBlocking {
        val originalAppt = Appointment(
            id = 1,
            type = "vet",
            vetName = "Dr. Lee",
            clinicName = "Happy Pets",
            address = "123 Pet Street",
            date = "2025-12-31", // Only when the date before this date, the upcomingVetAppointment will have value
            time = "10:00",
            completed = false
        )

        viewModel.addAppointment(originalAppt)
        kotlinx.coroutines.delay(100)

        val updatedAppt = originalAppt.copy(vetName = "Dr. Wang")
        viewModel.updateAppointment(updatedAppt)
        kotlinx.coroutines.delay(100)

        assertEquals(1, viewModel.upcomingVetAppointments.size)
        assertEquals("Dr. Wang", viewModel.upcomingVetAppointments[0].vetName)
    }

    @Test
    fun testDeleteAppointment() = runBlocking {
        val appt = Appointment(
            id = 1,
            type = "vet",
            vetName = "Dr. Lee",
            clinicName = "Happy Pets",
            address = "123 Pet Street",
            date = "2025-12-31",
            time = "10:00",
            completed = false
        )

        viewModel.addAppointment(appt)
        kotlinx.coroutines.delay(100)

        viewModel.deleteAppointment(appt)
        kotlinx.coroutines.delay(100)

        assertTrue(viewModel.upcomingVetAppointments.isEmpty())
    }
}
