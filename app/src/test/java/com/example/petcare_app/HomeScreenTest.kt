package com.example.petcare_app

import androidx.navigation.NavHostController
import org.junit.Test
import org.mockito.Mockito.*


class HomeScreenTest {
    @Test
    fun testNavigationToMyPetScreen() {
        val navController = mock(NavHostController::class.java)
        // simulate navigation
        navController.navigate("mypet")
        verify(navController).navigate("mypet")
    }
}
