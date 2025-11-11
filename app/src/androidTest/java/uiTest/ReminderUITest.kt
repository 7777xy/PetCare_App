package uiTest

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.rememberNavController
import com.example.petcare_app.ui.screens.ReminderScreen
import org.junit.Rule
import org.junit.Test

class ReminderUITest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val fakeViewModel = FakeReminderViewModel()


    @Test
    fun testReminderScreenDisplaysReminders() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            ReminderScreen(navController, fakeViewModel)
        }

        // Check that initial upcoming reminder is displayed
        composeTestRule.onNodeWithText("Upcoming").assertIsDisplayed()
    }

    @Test
    fun testAddReminder() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            ReminderScreen(navController, fakeViewModel)
        }

        // Open add reminder dialog
        composeTestRule.onNodeWithContentDescription("Add Reminder").performClick()

        // Fill in title
        composeTestRule.onNode(hasText("Title")).performTextInput("New Reminder")

        // Confirm save
        composeTestRule.onNodeWithText("Save").performClick()

        // Check that new reminder is added
        composeTestRule.onNodeWithText("New Reminder").assertIsDisplayed()
    }


    @Test
    fun testDeleteReminder() {
        composeTestRule.setContent {
            ReminderScreen(
                navController = rememberNavController(),
                viewModel = fakeViewModel
            )
        }

        // Delete first reminder
        composeTestRule.onAllNodes(hasContentDescription("Delete"))[0].performClick()

        // Assert the first reminder no longer exists
        composeTestRule.onNodeWithText("Vet Checkup").assertDoesNotExist()
    }
}
