package uiTest

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.*
import androidx.navigation.compose.rememberNavController
import com.example.petcare_app.ui.screens.MyPetScreen
import org.junit.Rule
import org.junit.Test

class MyPetUITest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun getFakeViewModel() = FakeMyPetViewModel()

    @Test
    fun displaysEmptyStateInitially() {
        val viewModel = getFakeViewModel()
        composeTestRule.setContent {
            MyPetScreen(navController = rememberNavController(), viewModel = viewModel)
        }

        composeTestRule.onNodeWithText("No pet details available. Tap + to add a pet.").assertIsDisplayed()
    }

    @Test
    fun addPetButtonAddsNewPet() {
        val viewModel = getFakeViewModel()
        composeTestRule.setContent {
            MyPetScreen(navController = rememberNavController(), viewModel = viewModel)
        }

        composeTestRule.onNodeWithContentDescription("Add Pet").performClick()
        composeTestRule.onNodeWithText("Details of Unnamed Pet").assertIsDisplayed()
    }

    @Test
    fun deletePetRemovesCard() {
        val viewModel = getFakeViewModel()
        viewModel.setPetsForTest(listOf(
            viewModel.pets.firstOrNull() ?: com.example.petcare_app.ui.screens.Pet(
                1,
                com.example.petcare_app.ui.screens.BasicInfo("Buddy","2","Dog","10kg","Male","Beagle","Brown"),
                com.example.petcare_app.ui.screens.Lifestyle("Walk daily","Dry food"),
                com.example.petcare_app.ui.screens.History("None","Up to date")
            )
        ))

        composeTestRule.setContent {
            MyPetScreen(navController = rememberNavController(), viewModel = viewModel)
        }

        // Delete button of first pet card
        composeTestRule.onAllNodesWithContentDescription("Delete Pet")[0].performClick()
        composeTestRule.onNodeWithText("Details of Buddy").assertDoesNotExist()
    }

    @Test
    fun expandAndCollapsePetDetails() {
        val viewModel = getFakeViewModel()
        viewModel.setPetsForTest(listOf(
            com.example.petcare_app.ui.screens.Pet(
                1,
                com.example.petcare_app.ui.screens.BasicInfo("Buddy","2","Dog","10kg","Male","Beagle","Brown"),
                com.example.petcare_app.ui.screens.Lifestyle("Walk daily","Dry food"),
                com.example.petcare_app.ui.screens.History("None","Up to date")
            )
        ))

        composeTestRule.setContent {
            MyPetScreen(navController = rememberNavController(), viewModel = viewModel)
        }

        // Expand
        composeTestRule.onAllNodesWithContentDescription("Expand")[0].performClick()
        composeTestRule.onNodeWithText("Age: 2").assertIsDisplayed()

        // Collapse
        composeTestRule.onAllNodesWithContentDescription("Collapse")[0].performClick()
        composeTestRule.onNodeWithText("Age: 2").assertDoesNotExist()
    }
}
