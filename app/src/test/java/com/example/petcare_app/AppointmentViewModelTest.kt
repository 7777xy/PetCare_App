package com.example.petcare_app

import android.util.Log
import com.example.petcare_app.data.AppDatabase
import com.example.petcare_app.data.AppointmentDao
import com.example.petcare_app.ui.screens.Appointment
import com.example.petcare_app.viewmodel.AppointmentViewModel
import com.example.petcare_app.data.toAppointmentEntity
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AppointmentViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: AppointmentViewModel
    private val mockDb = mockk<AppDatabase>(relaxed = true)
    private val mockDao = mockk<AppointmentDao>(relaxed = true)

    @Before
    fun setup() {
        every { mockDb.appointmentDao() } returns mockDao

        // Stub android.util.Log
        mockkStatic(Log::class)
        every { Log.d(any<String>(), any<String>()) } returns 0
        every { Log.w(any<String>(), any<String>()) } returns 0
        every { Log.e(any<String>(), any<String>(), any<Throwable>()) } returns 0
    }

    @Test
    fun testAddAppointment() = runTest {
        val appt = Appointment(
            id = 1,
            type = "vet",
            vetName = "Dr. Lee",
            clinicName = "Happy Pets",
            address = "123 Pet Street",
            date = "2025-11-07",
            time = "10:00"
        )

        coEvery { mockDao.getAll() } returns emptyList() andThen listOf(appt.toAppointmentEntity())
        coEvery { mockDao.insert(any()) } just Runs

        viewModel = AppointmentViewModel(mockDb)
        advanceUntilIdle()

        viewModel.addAppointment(appt)
        advanceUntilIdle()

        assertEquals(1, viewModel.upcomingVetAppointments.size)
        assertEquals("Dr. Lee", viewModel.upcomingVetAppointments[0].vetName)
    }

    @Test
    fun testMarkCompleted() = runTest {
        val appt = Appointment(
            id = 1,
            type = "vet",
            vetName = "Dr. Lee",
            clinicName = "Happy Pets",
            address = "123 Pet Street",
            date = "2025-11-07",
            time = "10:00"
        )
        val completedApptEntity = appt.copy(completed = true).toAppointmentEntity()

        coEvery { mockDao.getAll() } returns listOf(appt.toAppointmentEntity()) andThen listOf(completedApptEntity)
        coEvery { mockDao.update(any()) } just Runs

        viewModel = AppointmentViewModel(mockDb)
        advanceUntilIdle()

        viewModel.markAppointmentCompleted(appt.copy(completed = true))
        advanceUntilIdle()

        assertTrue(viewModel.upcomingVetAppointments.isEmpty())
        assertEquals(1, viewModel.completedAppointments.size)
        assertTrue(viewModel.completedAppointments[0].completed)
    }

    @Test
    fun testUpdateAppointment() = runTest {
        val originalAppt = Appointment(
            id = 1,
            type = "vet",
            vetName = "Dr. Lee",
            clinicName = "Happy Pets",
            address = "123 Pet Street",
            date = "2025-11-07",
            time = "10:00"
        )
        val updatedAppt = originalAppt.copy(vetName = "Dr. Wang")

        coEvery { mockDao.getAll() } returns listOf(originalAppt.toAppointmentEntity()) andThen listOf(updatedAppt.toAppointmentEntity())
        coEvery { mockDao.update(any()) } just Runs

        viewModel = AppointmentViewModel(mockDb)
        advanceUntilIdle()

        viewModel.updateAppointment(updatedAppt)
        advanceUntilIdle()

        assertEquals(1, viewModel.upcomingVetAppointments.size)
        assertEquals("Dr. Wang", viewModel.upcomingVetAppointments[0].vetName)
    }

    @Test
    fun testDeleteAppointment() = runTest {
        val appt = Appointment(
            id = 1,
            type = "vet",
            vetName = "Dr. Lee",
            clinicName = "Happy Pets",
            address = "123 Pet Street",
            date = "2025-11-07",
            time = "10:00"
        )

        coEvery { mockDao.getAll() } returns listOf(appt.toAppointmentEntity()) andThen emptyList()
        coEvery { mockDao.delete(any()) } just Runs

        viewModel = AppointmentViewModel(mockDb)
        advanceUntilIdle()

        viewModel.deleteAppointment(appt)
        advanceUntilIdle()

        assertTrue(viewModel.upcomingVetAppointments.isEmpty())
    }
}

