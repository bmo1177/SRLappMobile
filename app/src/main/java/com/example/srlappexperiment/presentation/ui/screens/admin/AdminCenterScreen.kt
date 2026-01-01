package com.example.srlappexperiment.presentation.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
fun AdminCenterScreen(
    onNavigateToUserManagement: () -> Unit,
    onNavigateToCMS: () -> Unit,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin Center", fontWeight = FontWeight.Bold) },
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
            // Metrics Section
            Text("Platform Overview", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                AdminMetricCard("Users", "45.2K", "+12%", Success, Modifier.weight(1f))
                AdminMetricCard("Revenue", "$23.4K", "+18%", PrimaryBlue, Modifier.weight(1f))
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                AdminMetricCard("Active", "12.5K", "+8%", PrimaryPurple, Modifier.weight(1f))
                AdminMetricCard("Churn", "2.3%", "-0.5%", com.example.srlappexperiment.ui.theme.Error, Modifier.weight(1f))
            }

            // Quick Management Actions
            Text("Management", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            
            ManagementActionItem(
                title = "User Management",
                subtitle = "Manage 45,234 registered learners",
                icon = Icons.Default.Group,
                color = PrimaryPurple,
                onClick = onNavigateToUserManagement
            )
            ManagementActionItem(
                title = "Content CMS",
                subtitle = "Manage sections, lessons, and vocabulary",
                icon = Icons.Default.AutoStories,
                color = PrimaryBlue,
                onClick = onNavigateToCMS
            )
            ManagementActionItem(
                title = "System Health",
                subtitle = "API: 12ms | DB: 8ms | Healthy",
                icon = Icons.Default.HealthAndSafety,
                color = Success,
                onClick = { }
            )

            // Live Activity Feed
            Text("Live Activity (Last 5m)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                ActivityLogItem("John Smith", "completed Lesson 15", "2m ago")
                ActivityLogItem("Maria Garcia", "upgraded to Premium", "12m ago")
                ActivityLogItem("Ahmed Hassan", "started English Course", "15m ago")
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun AdminMetricCard(title: String, value: String, change: String, color: Color, modifier: Modifier) {
    ModernCard(modifier = modifier, backgroundColor = SurfaceLight) {
        Column(Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.labelMedium, color = TextSecondary)
            Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold, color = TextPrimary)
            Text(change, style = MaterialTheme.typography.labelSmall, color = color, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ManagementActionItem(title: String, subtitle: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, onClick: () -> Unit) {
    ModernCard(modifier = Modifier.fillMaxWidth().clickable { onClick() }, backgroundColor = SurfaceLight) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(40.dp).background(color.copy(alpha = 0.1f), RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = color)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold)
                Text(subtitle, fontSize = 12.sp, color = TextSecondary)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.LightGray)
        }
    }
}

@Composable
fun ActivityLogItem(name: String, action: String, time: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Box(Modifier.size(8.dp).background(Success, CircleShape))
        Spacer(modifier = Modifier.width(12.dp))
        Text(name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Text(" $action", fontSize = 14.sp, color = TextSecondary)
        Spacer(modifier = Modifier.weight(1f))
        Text(time, fontSize = 12.sp, color = Color.Gray)
    }
}
