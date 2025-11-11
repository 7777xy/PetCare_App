package uiTest

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.petcare_app.ui.screens.Pet
import com.example.petcare_app.viewmodel.MyPetViewModel

// Fake ViewModel for UI testing — no database needed
class FakeMyPetViewModel : MyPetViewModel(null) {

    private val _pets = mutableStateOf(listOf<Pet>())
    override var pets by _pets

    override fun addPet(newPet: Pet) {
        pets = pets + newPet.copy(id = pets.size + 1)
    }

    override fun updatePet(updatedPet: Pet) {
        pets = pets.map { if (it.id == updatedPet.id) updatedPet else it }
    }

    override fun deletePet(pet: Pet) {
        pets = pets.filterNot { it.id == pet.id }
    }

    override fun setPetsForTest(testPets: List<Pet>) {
        pets = testPets
    }
}
