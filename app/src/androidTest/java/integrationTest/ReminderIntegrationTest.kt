package integrationTest

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.example.petcare_app.data.AppDatabase
import com.example.petcare_app.ui.screens.Reminder
import com.example.petcare_app.viewmodel.ReminderViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ReminderIntegrationTest {

    private lateinit var database: AppDatabase
    private lateinit var viewModel: ReminderViewModel

    @Before
    fun setup() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries() // Only for testing
            .build()
        viewModel = ReminderViewModel(database)
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun testAddAppointment() = runBlocking {
        val reminder = Reminder(1, "Sleep", "2025-11-07 10:00", false)

        viewModel.addReminder(reminder)

        // Wait a little for ViewModel to refresh LiveData/StateFlow
        kotlinx.coroutines.delay(100)

        assertEquals(1, viewModel.upcomingReminders.size)
        assertEquals("Sleep", viewModel.upcomingReminders[0].title)

    }

    @Test
    fun testMarkCompleted() = runBlocking {
        val reminder = Reminder(1, "Sleep", "2025-11-07 10:00", false)

        viewModel.addReminder(reminder)

        // mark completed
        val completedReminder = reminder.copy(completed = true)
        viewModel.markReminderCompleted(completedReminder)

        // wait for LiveData/StateFlow to update
        kotlinx.coroutines.delay(100)

        // assert
        assertTrue(viewModel.upcomingReminders.isEmpty())
        assertEquals(1, viewModel.completedReminders.size)
        assertTrue(viewModel.completedReminders[0].completed)
    }


    @Test
    fun testUpdateAppointment() = runBlocking {
        val originReminder = Reminder(1, "Sleep", "2025-11-07 10:00", false)

        viewModel.addReminder(originReminder)
        kotlinx.coroutines.delay(100)

        val updatedReminder = originReminder.copy(title = "Eat")
        viewModel.updateReminder(updatedReminder)
        kotlinx.coroutines.delay(100)

        assertEquals(1, viewModel.upcomingReminders.size)
        assertEquals("Eat", viewModel.upcomingReminders[0].title)
    }

    @Test
    fun testDeleteAppointment() = runBlocking {
        val reminder = Reminder(1, "Sleep", "2025-11-07 10:00", false)

        viewModel.addReminder(reminder)
        kotlinx.coroutines.delay(100)

        viewModel.deleteReminder(reminder)
        kotlinx.coroutines.delay(100)

        assertTrue(viewModel.upcomingReminders.isEmpty())
    }
}
