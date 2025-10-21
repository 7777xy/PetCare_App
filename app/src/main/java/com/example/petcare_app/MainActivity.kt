package com.example.petcare_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.petcare_app.ui.components.BottomBar
import com.example.petcare_app.ui.navigation.NavHostContainer
import com.example.petcare_app.ui.theme.PetCare_AppTheme
import androidx.room.Room
import com.example.petcare_app.data.AppDatabase
import com.example.petcare_app.viewmodel.MyPetViewModel
import com.example.petcare_app.viewmodel.MyPetViewModelFactory
import com.example.petcare_app.viewmodel.AppointmentViewModel
import com.example.petcare_app.viewmodel.AppointmentViewModelFactory
import com.example.petcare_app.viewmodel.ReminderViewModel
import com.example.petcare_app.viewmodel.ReminderViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the Room database here
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "petcare.db"  // database file name
        ).build()

        setContent {
            PetCareApp(db)
        }
    }
}

@Composable
fun PetCareApp(db: AppDatabase) {
    PetCare_AppTheme {
        val navController = rememberNavController()
        val myPetViewModel: MyPetViewModel = viewModel(
            factory = MyPetViewModelFactory(db)
        )
        val appointmentViewModel: AppointmentViewModel = viewModel(
            factory = AppointmentViewModelFactory(db)
        )
        val reminderViewModel: ReminderViewModel = viewModel(
            factory = ReminderViewModelFactory(db)
        )
        Scaffold(
            bottomBar = { BottomBar(navController) }
        ) { innerPadding ->
            NavHostContainer(navController, Modifier.padding(innerPadding), myPetViewModel, appointmentViewModel, reminderViewModel)
        }
    }
}

