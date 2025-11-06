package integrationTest

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.example.petcare_app.data.AppDatabase
import com.example.petcare_app.ui.screens.BasicInfo
import com.example.petcare_app.ui.screens.History
import com.example.petcare_app.ui.screens.Lifestyle
import com.example.petcare_app.ui.screens.Pet
import com.example.petcare_app.viewmodel.MyPetViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MyPetIntegrationTest {

    private lateinit var database: AppDatabase
    private lateinit var viewModel: MyPetViewModel

    @Before
    fun setup() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries() // Only for testing
            .build()
        viewModel = MyPetViewModel(database)
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun testAddPetIntegration() = runBlocking {
        val pet = Pet(
            1,
            BasicInfo("Buddy", "2", "Dog", "10kg", "Male", "Beagle", "Brown"),
            Lifestyle("Walk daily", "Dry food"),
            History("None", "Up to date")
        )

        viewModel.addPet(pet)
        // Wait a little for ViewModel to refresh LiveData/StateFlow
        kotlinx.coroutines.delay(100)

        val pets = viewModel.pets
        assertEquals(1, pets.size)
        assertEquals("Buddy", pets[0].basicInfo.name)
    }

    @Test
    fun testUpdatePetIntegration() = runBlocking {
        val pet = Pet(
            1,
            BasicInfo("Buddy", "2", "Dog", "10kg", "Male", "Beagle", "Brown"),
            Lifestyle("Walk daily", "Dry food"),
            History("None", "Up to date")
        )

        viewModel.addPet(pet)
        kotlinx.coroutines.delay(100)

        val updatedPet = pet.copy(basicInfo = pet.basicInfo.copy(name = "Max"))
        viewModel.updatePet(updatedPet)
        kotlinx.coroutines.delay(100)

        val pets = viewModel.pets
        assertEquals(1, pets.size)
        assertEquals("Max", pets[0].basicInfo.name)
    }

    @Test
    fun testDeletePetIntegration() = runBlocking {
        val pet = Pet(
            1,
            BasicInfo("Buddy", "2", "Dog", "10kg", "Male", "Beagle", "Brown"),
            Lifestyle("Walk daily", "Dry food"),
            History("None", "Up to date")
        )

        viewModel.addPet(pet)
        kotlinx.coroutines.delay(100)

        viewModel.deletePet(pet)
        kotlinx.coroutines.delay(100)

        val pets = viewModel.pets
        assertTrue(pets.isEmpty())
    }
}
