package com.example.petcare_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.petcare_app.ui.components.BottomBar
import com.example.petcare_app.ui.navigation.NavHostContainer
import com.example.petcare_app.ui.theme.PetCare_AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PetCareApp()
        }
    }
}

@Composable
fun PetCareApp() {
    PetCare_AppTheme {
        val navController = rememberNavController()
        Scaffold(
            bottomBar = { BottomBar(navController) }
        ) { innerPadding ->
            NavHostContainer(navController, Modifier.padding(innerPadding))
        }
    }
}