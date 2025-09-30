package com.example.petcare_app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import androidx.compose.material3.*
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.example.petcare_app.R
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.text.style.TextAlign


@Composable
fun HomeScreen(navController: NavHostController) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Welcome message
            Text(
                text = "Welcome to PetCare! \uD83D\uDC3E",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Texts between title and profile
            Text(
                text = "This is a multifunctional and comprehensive APP, helping user to easily monitor his pets’ fitness, receive timely reminders for \n" +
                        "appointment and vaccination, and keep organized records of diet and activity.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp) // Optional: add some horizontal padding
            )

            Spacer(modifier = Modifier.height(18.dp))

            // 2. Profile summary
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                // horizontalArrangement = Arrangement.SpaceBetween, // push items to left & right
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left profile
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_sample_user_profile), // replace with your image
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Alex",
                            fontSize = 20.sp,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                        )
                        Text(
                            text = "22 years old",
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                // Right profile
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_sample_pet_profile), // replace with your image
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Jack",
                            fontSize = 20.sp,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                        )
                        Text(
                            text = "2 years old",
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(18.dp))

            // 3. Upcoming Appointments Section
            SectionCard(
                title = "Upcoming Appointments",
                items = listOf("Vet Visit - Oct 5, 10:00 AM", "Vaccination - Oct 12, 3:00 PM"),
                actionLabel = "Schedule Appointment",
                onActionClick = {
                    navController.navigate("appointment") {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )

            Spacer(modifier = Modifier.height(28.dp))

            // 4. Pending Reminders Section
            SectionCard(
                title = "Pending Reminders",
                items = listOf("Deworming Medicine - Oct 7 ~ Oct 13", "Grooming Session - Oct 9, 5:00 PM"),
                actionLabel = "Add Reminder",
                onActionClick = {
                    navController.navigate("reminder") {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )

        }
    }
}

/**
 * Reusable section card
 */
@Composable
fun SectionCard(
    title: String,
    items: List<String>,
    actionLabel: String,
    onActionClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
            Text(text = title, style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)

            Spacer(modifier = Modifier.height(12.dp))


            if (items.isNotEmpty()) {
                items.take(2).forEach { item -> // Limit to 2 items as before, or adjust
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "• ", // Bullet point
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary // Optional: Color for bullet
                        )
                        Text(
                            text = item,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface // Main text color for items
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp)) // Small space between items
                }
            } else {
                Text(
                    text = "No items to display.", // Placeholder if items list is empty
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onActionClick,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(actionLabel, style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}