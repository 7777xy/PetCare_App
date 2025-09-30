package com.example.petcare_app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.navigation.NavHostController


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
    var exerciseRoutine: String = "",
    var diet: String = ""
)

data class History(
    var medicalHistory: String = "",
    var vaccinationHistory: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPetScreen(navController: NavHostController) {
    var pets by remember { mutableStateOf(listOf<Pet>()) }

    Scaffold(
        topBar = {
            TopAppBar(title = {Text(
                text = "My Pets",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary) })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                pets = pets + Pet(
                    id = pets.size + 1,
                    basicInfo = BasicInfo("", "", "", "", "", "", ""),
                    lifestyle = Lifestyle(),
                    history = History()
                )
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add Pet Details")
            }
        }
    ) { padding ->
        MyPetsContent(
            pets = pets,
            onUpdateBasic = { pet, updated ->
                pets = pets.map { if (it.id == pet.id) it.copy(basicInfo = updated) else it }
            },
            onUpdateLifestyle = { pet, updated ->
                pets = pets.map { if (it.id == pet.id) it.copy(lifestyle = updated) else it }
            },
            onUpdateHistory = { pet, updated ->
                pets = pets.map { if (it.id == pet.id) it.copy(history = updated) else it }
            },
            modifier = Modifier.padding(padding)
        )
    }
}


@Composable
fun MyPetsContent(
    pets: List<Pet>,
    onUpdateBasic: (Pet, BasicInfo) -> Unit,
    onUpdateLifestyle: (Pet, Lifestyle) -> Unit,
    onUpdateHistory: (Pet, History) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
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
                BasicInfoCard(pet) { updated -> onUpdateBasic(pet, updated) }
                LifestyleCard(pet) { updated -> onUpdateLifestyle(pet, updated) }
                HistoryCard(pet) { updated -> onUpdateHistory(pet, updated) }
            }
        }
    }
}


@Composable
fun BasicInfoCard(pet: Pet, onUpdate: (BasicInfo) -> Unit) {
    var showDialog by remember { mutableStateOf(false) }

    InfoCard(title = "🐾 Basic Info (${pet.basicInfo.name.ifBlank { "Unnamed Pet" }})") {
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
            onSave = {
                onUpdate(it)
                showDialog = false
            }
        )
    }

    Spacer(Modifier.height(16.dp))
}


@Composable
fun LifestyleCard(pet: Pet, onUpdate: (Lifestyle) -> Unit) {
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
            onSave = {
                onUpdate(it)
                showDialog = false
            }
        )
    }
    Spacer(Modifier.height(16.dp))
}


@Composable
fun HistoryCard(pet: Pet, onUpdate: (History) -> Unit) {
    var showDialog by remember { mutableStateOf(false) }

    InfoCard(title = "📜 History") {
        Text("Medical History: ${pet.history.medicalHistory.ifBlank { "None" }}")
        Text("Vaccination History: ${pet.history.vaccinationHistory.ifBlank { "None" }}")

        Spacer(Modifier.height(8.dp))
        Row {
            Button(onClick = { showDialog = true }) { Text("Edit") }
        }
    }

    if (showDialog) {
        HistoryDialog(
            initial = pet.history,
            onDismiss = { showDialog = false },
            onSave = {
                onUpdate(it)
                showDialog = false
            }
        )
    }
    Spacer(Modifier.height(16.dp))
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
            Text(title, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(6.dp))
            content()
        }
    }
}


