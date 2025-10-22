package com.example.petcare_app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.petcare_app.ui.components.BottomBar
import com.example.petcare_app.ui.navigation.NavHostContainer
import com.example.petcare_app.ui.theme.PetCare_AppTheme
import androidx.room.Room
import com.example.petcare_app.data.AppDatabase
import com.example.petcare_app.utils.NotificationHelper
import com.example.petcare_app.viewmodel.MyPetViewModel
import com.example.petcare_app.viewmodel.MyPetViewModelFactory
import com.example.petcare_app.viewmodel.AppointmentViewModel
import com.example.petcare_app.viewmodel.AppointmentViewModelFactory
import com.example.petcare_app.viewmodel.ReminderViewModel
import com.example.petcare_app.viewmodel.ReminderViewModelFactory
import android.app.AlarmManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the Room database here
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "petcare.db"
        ).build()

        // ✅ Create Notification Channel for Android 8+
        NotificationHelper.createNotificationChannel(this)

        // ✅ Request notification permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        // ✅ Request exact alarm permission on Android 12+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                android.util.Log.w("MainActivity", "Requesting SCHEDULE_EXACT_ALARM permission")
                requestExactAlarmPermissionLauncher.launch(android.Manifest.permission.SCHEDULE_EXACT_ALARM)
            }
        }

        setContent {
            PetCareApp(db)
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                android.util.Log.d("MainActivity", "Notification permission granted")
            } else {
                android.util.Log.w("MainActivity", "Notification permission denied")
            }
        }

    private val requestExactAlarmPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                android.util.Log.d("MainActivity", "SCHEDULE_EXACT_ALARM permission granted")
            } else {
                android.util.Log.w("MainActivity", "SCHEDULE_EXACT_ALARM permission denied - will use inexact alarms")
            }
        }


    @Composable
    fun PetCareApp(db: AppDatabase) {
        PetCare_AppTheme {
            val navController = rememberNavController()
            val myPetViewModel: MyPetViewModel = viewModel(factory = MyPetViewModelFactory(db))
            val appointmentViewModel: AppointmentViewModel =
                viewModel(factory = AppointmentViewModelFactory(db))
            val reminderViewModel: ReminderViewModel =
                viewModel(factory = ReminderViewModelFactory(db))

            Scaffold(bottomBar = { BottomBar(navController) }) { innerPadding ->
                NavHostContainer(
                    navController,
                    Modifier.padding(innerPadding),
                    myPetViewModel,
                    appointmentViewModel,
                    reminderViewModel
                )
            }
        }
    }
}



