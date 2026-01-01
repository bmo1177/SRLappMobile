package com.example.srlappexperiment.presentation.ui.screens.support

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.srlappexperiment.ui.theme.*
import com.example.srlappexperiment.presentation.ui.components.ModernCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupportPortalScreen(
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Support Portal", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundLight)
            )
        },
        containerColor = BackgroundLight
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text("How can we help you?", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            
            // Search Bar Placeholder
            ModernCard(modifier = Modifier.fillMaxWidth(), backgroundColor = SurfaceLight) {
                Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary)
                    Spacer(Modifier.width(12.dp))
                    Text("Search for articles...", color = TextSecondary)
                }
            }

            // Quick Actions
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SupportActionCard("My Tickets", Icons.Default.ConfirmationNumber, Modifier.weight(1f))
                SupportActionCard("Submit Ticket", Icons.Default.EditNote, Modifier.weight(1f))
            }

            // FAQ Section
            Text("Top Questions", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                FAQItem("How do I cancel my subscription?")
                FAQItem("Can I use the app offline?")
                FAQItem("How to reset my learning progress?")
                FAQItem("Is the English course certified?")
            }

            // Contact Support
            ModernCard(modifier = Modifier.fillMaxWidth(), backgroundColor = PrimaryPurple.copy(alpha = 0.05f)) {
                Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Still need help?", fontWeight = FontWeight.Bold)
                    Text("Our support agents are online", fontSize = 12.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {},
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple)
                    ) {
                        Text("Chat with Us")
                    }
                }
            }
        }
    }
}

@Composable
fun SupportActionCard(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier) {
    ModernCard(modifier = modifier, backgroundColor = SurfaceLight) {
        Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, contentDescription = null, tint = PrimaryPurple)
            Spacer(modifier = Modifier.height(8.dp))
            Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
    }
}

@Composable
fun FAQItem(question: String) {
    ModernCard(modifier = Modifier.fillMaxWidth(), backgroundColor = SurfaceLight) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(question, modifier = Modifier.weight(1f), fontWeight = FontWeight.SemiBold)
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.LightGray)
        }
    }
}
