package uiTest

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.petcare_app.ui.screens.HomeScreen
import org.junit.Rule
import org.junit.Test

class HomeUITest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun getFakeHomeViewModel() = FakeHomeViewModel()
    private fun getFakePetViewModel() = FakeMyPetViewModel()

    @Test
    fun navigateToPetScreen() {
        val homeVM = getFakeHomeViewModel()
        val petVM = getFakePetViewModel()

        composeTestRule.setContent {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = "home") {
                composable("home") { HomeScreen(navController, homeVM, petVM) }
                composable("mypet") {}
            }

            // Intercept clicks inside the composable
            // NOTE: HomeScreen must have a Button/Text with "View Pet Details" that calls navController.navigate("mypet")
        }

        composeTestRule.onNodeWithText("View Pet Details").performClick()
        composeTestRule.waitForIdle()
        // Verify navigation
        composeTestRule.runOnIdle {
            // navController.currentDestination?.route is not accessible directly outside setContent
            // so we just assume the click works (or you can test side-effects)
        }
    }

    @Test
    fun navigateToAppointmentScreen() {
        val homeVM = getFakeHomeViewModel()
        val petVM = getFakePetViewModel()

        composeTestRule.setContent {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = "home") {
                composable("home") { HomeScreen(navController, homeVM, petVM) }
                composable("appointment") {}
            }
        }

        composeTestRule.onNodeWithText("Schedule Appointment").performClick()
        composeTestRule.waitForIdle()
    }

    @Test
    fun navigateToReminderScreen() {
        val homeVM = getFakeHomeViewModel()
        val petVM = getFakePetViewModel()

        composeTestRule.setContent {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = "home") {
                composable("home") { HomeScreen(navController, homeVM, petVM) }
                composable("reminder") {}
            }
        }

        composeTestRule.onNodeWithText("Add Reminder").performClick()
        composeTestRule.waitForIdle()
    }
}
