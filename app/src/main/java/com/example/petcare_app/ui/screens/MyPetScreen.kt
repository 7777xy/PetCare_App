package com.example.petcare_app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavHostController
import com.example.petcare_app.viewmodel.MyPetViewModel


data class Pet(
    val id: Int,
    var basicInfo: BasicInfo,
    var lifestyle: Lifestyle,
    var history: History
)

data class BasicInfo(
    var name: String,
    var age: String,
    var species: String,
    var weight: String,
    var gender: String,
    var breed: String,
    var color: String
)

data class Lifestyle(
    var exerciseRoutine: String,
    var diet: String
)

data class History(
    var medicalHistory: String,
    var vaccinationHistory: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPetScreen(navController: NavHostController, viewModel: MyPetViewModel) {
    val pets = viewModel.pets

    Scaffold(
        topBar = {
            TopAppBar(title = {Text(
                text = "My Pets",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary) })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                viewModel.addPet(
                    Pet(
                        id = pets.size + 1,
                        basicInfo = BasicInfo("", "", "", "", "", "", ""),
                        lifestyle = Lifestyle("", ""),
                        history = History("", "")
                    )
                )
            }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Pet")
            }
        }
    ) { padding ->
        MyPetsContent(
            pets = pets,
            viewModel = viewModel,
            modifier = Modifier.padding(padding)
        )
    }
}


@Composable
fun MyPetsContent(
    pets: List<Pet>,
    viewModel: MyPetViewModel,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        if (pets.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillParentMaxSize() // Make the Box take the whole LazyColumn space
                        .padding(16.dp),
                    contentAlignment = Alignment.Center // Center content horizontally and vertically
                ) {
                    Text("No pet details available. Tap + to add a pet.")
                }
            }
        }
        else {
            items(pets, key = { it.id }) { pet ->
                PetNameCard(pet, viewModel)
            }
        }
    }
}

@Composable
fun PetNameCard(pet: Pet, viewModel: MyPetViewModel) {
    var expanded by remember { mutableStateOf(false) } // Track expand/collapse state

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) { // --- Header Row (Pet name + expand + delete) ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, end = 4.dp), // Adjust padding for visual balance
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Details of ${pet.basicInfo.name.ifBlank { "Unnamed Pet" }}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f) // Allow text to take available space
                )
                Row {
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(
                            imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = if (expanded) "Collapse" else "Expand"
                        )
                    }
                    IconButton(onClick = { viewModel.deletePet(pet) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Pet")
                    }
                }
            }
            // --- Expandable Section ---
            if (expanded) {
                Spacer(Modifier.height(12.dp))
                BasicInfoCard(pet, viewModel)
                LifestyleCard(pet, viewModel)
                HistoryCard(pet, viewModel)
            }
        }
    }
}

@Composable
fun BasicInfoCard(pet: Pet, viewModel: MyPetViewModel) {
    var showDialog by remember { mutableStateOf(false) }

    InfoCard(title = "🐾 Basic Info") {
        Text("Age: ${pet.basicInfo.age}")
        Text("Species: ${pet.basicInfo.species}")
        Text("Weight: ${pet.basicInfo.weight}")
        Text("Gender: ${pet.basicInfo.gender}")
        Text("Breed: ${pet.basicInfo.breed}")
        Text("Color: ${pet.basicInfo.color}")

        Spacer(Modifier.height(8.dp))
        Row {
            Button(onClick = { showDialog = true }) { Text("Edit") }
        }
    }

    if (showDialog) {
        BasicInfoDialog(
            initial = pet.basicInfo,
            onDismiss = { showDialog = false },
            onSave = {updatedBasicInfo ->
                // Create a new Pet object with updated BasicInfo
                val updatedPet = pet.copy(basicInfo = updatedBasicInfo)
                viewModel.updatePet(updatedPet)  // call the ViewModel
                showDialog = false
            }
        )
    }

    Spacer(Modifier.height(8.dp))
}


@Composable
fun LifestyleCard(pet: Pet, viewModel: MyPetViewModel) {
    var showDialog by remember { mutableStateOf(false) }

    InfoCard(title = "🏃 Lifestyle") {
        Text("Exercise Routine: ${pet.lifestyle.exerciseRoutine.ifBlank { "Not set" }}")
        Text("Diet: ${pet.lifestyle.diet.ifBlank { "Not set" }}")

        Spacer(Modifier.height(8.dp))
        Row {
            Button(onClick = { showDialog = true }) { Text("Edit") }
        }
    }

    if (showDialog) {
        LifestyleDialog(
            initial = pet.lifestyle,
            onDismiss = { showDialog = false },
            onSave = { updatedLifestyle ->
                // Create a new Pet object with updated Lifestyle
                val updatedPet = pet.copy(lifestyle = updatedLifestyle)
                viewModel.updatePet(updatedPet)  // call the ViewModel
                showDialog = false
            }
        )
    }

    Spacer(Modifier.height(8.dp))
}



@Composable
fun HistoryCard(pet: Pet, viewModel: MyPetViewModel) {
    var showDialog by remember { mutableStateOf(false) }

    InfoCard(title = "📜 History") {
        Text("Medical History: ${pet.history.medicalHistory.ifBlank { "None" }}")
        Text("Vaccination History: ${pet.history.vaccinationHistory.ifBlank { "None" }}")

        Spacer(Modifier.height(8.dp))
        Row {
            Button(onClick = { showDialog = true }) { Text("Edit")
                }
        }
    }

    if (showDialog) {
        HistoryDialog(
            initial = pet.history,
            onDismiss = { showDialog = false },
            onSave = {updatedHistory ->
                // Create a new Pet object with updated History
                val updatedPet = pet.copy(history = updatedHistory)
                viewModel.updatePet(updatedPet)  // call the ViewModel
                showDialog = false
            }
        )
    }
    Spacer(Modifier.height(8.dp))
}


@Composable
fun BasicInfoDialog(
    initial: BasicInfo,
    onDismiss: () -> Unit,
    onSave: (BasicInfo) -> Unit
) {
    var name by remember { mutableStateOf(initial.name) }
    var age by remember { mutableStateOf(initial.age) }
    var species by remember { mutableStateOf(initial.species) }
    var weight by remember { mutableStateOf(initial.weight) }
    var gender by remember { mutableStateOf(initial.gender) }
    var breed by remember { mutableStateOf(initial.breed) }
    var color by remember { mutableStateOf(initial.color) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Basic Info") },
        text = {
            Column {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
                OutlinedTextField(value = age, onValueChange = { age = it }, label = { Text("Age") })
                OutlinedTextField(value = species, onValueChange = { species = it }, label = { Text("Species") })
                OutlinedTextField(value = weight, onValueChange = { weight = it }, label = { Text("Weight") })
                OutlinedTextField(value = gender, onValueChange = { gender = it }, label = { Text("Gender") })
                OutlinedTextField(value = breed, onValueChange = { breed = it }, label = { Text("Breed") })
                OutlinedTextField(value = color, onValueChange = { color = it }, label = { Text("Color") })
            }
        },
        confirmButton = {
            Button(onClick = {
                onSave(BasicInfo(name, age, species, weight, gender, breed, color))
            }) { Text("Save") }
        },
        dismissButton = { OutlinedButton(onClick = onDismiss) { Text("Cancel") } }
    )
}


@Composable
fun LifestyleDialog(
    initial: Lifestyle,
    onDismiss: () -> Unit,
    onSave: (Lifestyle) -> Unit
) {
    var exercise by remember { mutableStateOf(initial.exerciseRoutine) }
    var diet by remember { mutableStateOf(initial.diet) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Lifestyle") },
        text = {
            Column {
                OutlinedTextField(value = exercise, onValueChange = { exercise = it }, label = { Text("Exercise Routine") })
                OutlinedTextField(value = diet, onValueChange = { diet = it }, label = { Text("Diet") })
            }
        },
        confirmButton = {
            Button(onClick = {
                onSave(Lifestyle(exercise, diet))
            }) { Text("Save") }
        },
        dismissButton = { OutlinedButton(onClick = onDismiss) { Text("Cancel") } }
    )
}


@Composable
fun HistoryDialog(
    initial: History,
    onDismiss: () -> Unit,
    onSave: (History) -> Unit
) {
    var medical by remember { mutableStateOf(initial.medicalHistory) }
    var vaccination by remember { mutableStateOf(initial.vaccinationHistory) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit History") },
        text = {
            Column {
                OutlinedTextField(value = medical, onValueChange = { medical = it }, label = { Text("Medical History") })
                OutlinedTextField(value = vaccination, onValueChange = { vaccination = it }, label = { Text("Vaccination & Vet History") })
            }
        },
        confirmButton = {
            Button(onClick = {
                onSave(History(medical, vaccination))
            }) { Text("Save") }
        },
        dismissButton = { OutlinedButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun InfoCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            content()
        }
    }
}

