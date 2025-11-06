package com.example.petcare_app

import android.util.Log
import com.example.petcare_app.data.AppDatabase
import com.example.petcare_app.data.PetDao
import com.example.petcare_app.ui.screens.BasicInfo
import com.example.petcare_app.ui.screens.History
import com.example.petcare_app.ui.screens.Lifestyle
import com.example.petcare_app.ui.screens.Pet
import com.example.petcare_app.viewmodel.MyPetViewModel
import com.example.petcare_app.data.toPetEntity
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MyPetViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: MyPetViewModel
    private val mockDb = mockk<AppDatabase>(relaxed = true)
    private val mockDao = mockk<PetDao>(relaxed = true)

    @Before
    fun setup() {
        every { mockDb.petDao() } returns mockDao

        // Stub android.util.Log to prevent runtime exceptions
        mockkStatic(Log::class)
        every { Log.d(any<String>(), any<String>()) } returns 0
        every { Log.w(any<String>(), any<String>()) } returns 0
        every { Log.e(any<String>(), any<String>(), any<Throwable>()) } returns 0
    }

    @Test
    fun testAddPet() = runTest {
        val pet = Pet(
            1,
            BasicInfo("Buddy","2","Dog","10kg","Male","Beagle","Brown"),
            Lifestyle("Walk daily","Dry food"),
            History("None","Up to date")
        )

        coEvery { mockDao.getAll() } returns emptyList() andThen listOf(pet.toPetEntity())
        coEvery { mockDao.insert(any()) } just Runs

        viewModel = MyPetViewModel(mockDb)
        advanceUntilIdle() // Initial load

        viewModel.addPet(pet)
        advanceUntilIdle() // Process insert and refresh

        assertEquals(1, viewModel.pets.size)
        assertEquals("Buddy", viewModel.pets[0].basicInfo.name)
    }

    @Test
    fun testDeletePet() = runTest {
        val pet = Pet(
            1,
            BasicInfo("Buddy","2","Dog","10kg","Male","Beagle","Brown"),
            Lifestyle("Walk daily","Dry food"),
            History("None","Up to date")
        )

        coEvery { mockDao.getAll() } returns listOf(pet.toPetEntity()) andThen emptyList()
        coEvery { mockDao.delete(any()) } just Runs

        viewModel = MyPetViewModel(mockDb)
        advanceUntilIdle()

        viewModel.deletePet(pet)
        advanceUntilIdle()

        assertTrue(viewModel.pets.isEmpty())
    }

    @Test
    fun testUpdatePet() = runTest {
        val pet = Pet(
            1,
            BasicInfo("Buddy","2","Dog","10kg","Male","Beagle","Brown"),
            Lifestyle("Walk daily","Dry food"),
            History("None","Up to date")
        )
        val updatedPet = pet.copy(basicInfo = pet.basicInfo.copy(name = "Max"))

        coEvery { mockDao.getAll() } returns listOf(pet.toPetEntity()) andThen listOf(updatedPet.toPetEntity())
        coEvery { mockDao.update(any()) } just Runs

        viewModel = MyPetViewModel(mockDb)
        advanceUntilIdle()

        viewModel.updatePet(updatedPet)
        advanceUntilIdle()

        assertEquals(1, viewModel.pets.size)
        assertEquals("Max", viewModel.pets[0].basicInfo.name)
    }
}

