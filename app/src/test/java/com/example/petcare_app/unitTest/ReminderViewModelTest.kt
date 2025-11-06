package com.example.petcare_app.unitTest

import com.example.petcare_app.data.AppDatabase
import com.example.petcare_app.data.ReminderDao
import com.example.petcare_app.ui.screens.Reminder
import com.example.petcare_app.viewmodel.ReminderViewModel
import com.example.petcare_app.data.toReminderEntity
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import android.util.Log

@OptIn(ExperimentalCoroutinesApi::class)
class ReminderViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: ReminderViewModel
    private val mockDb = mockk<AppDatabase>(relaxed = true)
    private val mockDao = mockk<ReminderDao>(relaxed = true)

    @Before
    fun setup() {
        every { mockDb.reminderDao() } returns mockDao

        // Stub android.util.Log
        mockkStatic(Log::class)

        // specify exact parameter types
        every { Log.d(any<String>(), any<String>()) } returns 0
        every { Log.e(any<String>(), any<String>(), any<Throwable>()) } returns 0
        every { Log.w(any<String>(), any<String>()) } returns 0
    }

    @Test
    fun testAddReminder() = runTest {
        // Arrange
        val reminder = Reminder(1, "Sleep", "2025-11-07 10:00", false)
        // 1. Initial state is an empty list.
        // 2. After adding, the list contains the new reminder.
        coEvery { mockDao.getAll() } returns emptyList() andThen listOf(reminder.toReminderEntity())
        coEvery { mockDao.insert(any()) } just Runs

        // Act
        viewModel = ReminderViewModel(mockDb)
        advanceUntilIdle() // Let the ViewModel perform its initial load (gets emptyList).

        viewModel.addReminder(reminder)
        advanceUntilIdle() // Run the add operation and the subsequent data refresh.

        // Assert
        assertEquals(1, viewModel.upcomingReminders.size)
        assertEquals("Sleep", viewModel.upcomingReminders[0].title)
    }

    @Test
    fun testMarkCompleted() = runTest {
        // Arrange
        val reminder = Reminder(1, "Sleep", "2025-11-07 10:00", false)
        val completedReminderEntity = reminder.copy(completed = true).toReminderEntity()

        // 1. Initial state: The upcoming reminder exists.
        // 2. After update: The reminder is now marked as completed.
        coEvery { mockDao.getAll() } returns listOf(reminder.toReminderEntity()) andThen listOf(completedReminderEntity)
        coEvery { mockDao.update(any()) } just Runs

        // Act
        viewModel = ReminderViewModel(mockDb)
        advanceUntilIdle() // Initial load, upcomingReminders gets 1 item.

        viewModel.markReminderCompleted(reminder.copy(completed = true))
        advanceUntilIdle() // Process update and refresh lists.

        // Assert
        assertTrue(viewModel.upcomingReminders.isEmpty())
        assertEquals(1, viewModel.completedReminders.size)
        assertTrue(viewModel.completedReminders[0].completed)
    }

    @Test
    fun testUpdateReminder() = runTest {
        // Arrange
        val originalReminder = Reminder(1, "Sleep", "2025-11-07 10:00", false)
        val updatedReminder = originalReminder.copy(title = "Eat")

        // 1. Initial state: The original reminder exists.
        // 2. After update: The list contains the updated reminder.
        coEvery { mockDao.getAll() } returns listOf(originalReminder.toReminderEntity()) andThen listOf(updatedReminder.toReminderEntity())
        coEvery { mockDao.update(any()) } just Runs

        // Act
        viewModel = ReminderViewModel(mockDb)
        advanceUntilIdle() // Initial load.

        viewModel.updateReminder(updatedReminder)
        advanceUntilIdle() // Process update and refresh.

        // Assert
        assertEquals(1, viewModel.upcomingReminders.size)
        assertEquals("Eat", viewModel.upcomingReminders[0].title)
    }

    @Test
    fun testDeleteReminder() = runTest {
        // Arrange
        val reminder = Reminder(1, "Sleep", "2025-11-07 10:00", false)

        // 1. Initial state: The reminder exists.
        // 2. After deletion: The list is empty.
        coEvery { mockDao.getAll() } returns listOf(reminder.toReminderEntity()) andThen emptyList()
        coEvery { mockDao.delete(any()) } just Runs

        // Act
        viewModel = ReminderViewModel(mockDb)
        advanceUntilIdle() // Initial load.

        viewModel.deleteReminder(reminder)
        advanceUntilIdle() // Process deletion and refresh.

        // Assert
        assertTrue(viewModel.upcomingReminders.isEmpty())
    }
}
