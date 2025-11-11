package com.example.petcare_app.unitTest

import androidx.navigation.NavHostController
import org.junit.Test
import org.mockito.Mockito.*


class HomeUnitTest {
    @Test
    fun testNavigationToMyPetScreen() {
        val navController = mock(NavHostController::class.java)
        // simulate navigation
        navController.navigate("mypet")
        verify(navController).navigate("mypet")
    }

    @Test
    fun testNavigationToAppointmentScreen() {
        val navController = mock(NavHostController::class.java)
        // simulate navigation
        navController.navigate("appointment")
        verify(navController).navigate("appointment")
    }

    @Test
    fun testNavigationToReminderScreen() {
        val navController = mock(NavHostController::class.java)
        // simulate navigation
        navController.navigate("reminder")
        verify(navController).navigate("reminder")
    }
}
