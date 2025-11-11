package uiTest

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.rememberNavController
import com.example.petcare_app.ui.screens.Appointment
import com.example.petcare_app.ui.screens.AppointmentScreen
import com.example.petcare_app.ui.screens.ReminderScreen
import com.example.petcare_app.viewmodel.ReminderViewModel
import org.junit.Rule
import org.junit.Test

class AppointmentUITest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val fakeViewModel = FakeAppointmentViewModel()
    private val reminderViewModel = ReminderViewModel(db = null)



    @Test
    fun testAppointmentScreenDisplaysAppointments() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            AppointmentScreen(navController, fakeViewModel, reminderViewModel)
        }
        // Check that initial appointment is displayed
        composeTestRule.onNodeWithText("Upcoming Vet Appointments").assertIsDisplayed()
    }

    @Test
    fun addAppointmentShowsInList() {
        composeTestRule.setContent {
            AppointmentScreen(
                navController = rememberNavController(),
                viewModel = fakeViewModel,
                reminderViewModel = reminderViewModel
            )
        }

        // Open add dialog
        composeTestRule.onNode(hasContentDescription("Add Appointment")).performClick()

        // Fill in fields (simplified, assumes placeholders/textLabels)
        composeTestRule.onNode(hasText("Vet Name")).performTextInput("Dr. Lee")
        composeTestRule.onNode(hasText("Clinic Name")).performTextInput("Pet Care Center")
        composeTestRule.onNode(hasText("Address")).performTextInput("456 Pet Avenue")
        composeTestRule.onNode(hasText("Date")).performClick() // simulate picker selection
        composeTestRule.onNode(hasText("Time")).performClick()
        composeTestRule.onNode(hasText("Save")).performClick()

        // Assert new appointment shows
        composeTestRule.onNodeWithText("Dr. Lee").assertIsDisplayed()
        composeTestRule.onNodeWithText("Pet Care Center").assertIsDisplayed()
    }


    @Test
    fun deleteAppointmentRemovesIt() {
        composeTestRule.setContent {
            AppointmentScreen(
                navController = rememberNavController(),
                viewModel = fakeViewModel,
                reminderViewModel = reminderViewModel
            )
        }

        // Delete first appointment
        composeTestRule.onNode(hasContentDescription("Delete")).performClick()

        // Assert it no longer exists
        composeTestRule.onNodeWithText("Dr. Smith").assertDoesNotExist()
    }



}
