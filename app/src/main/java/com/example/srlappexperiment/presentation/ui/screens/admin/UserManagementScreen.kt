package com.example.srlappexperiment.presentation.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.srlappexperiment.ui.theme.*
import com.example.srlappexperiment.presentation.ui.components.ModernCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserManagementScreen(
    onNavigateToUserDetail: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("User Management", fontWeight = FontWeight.Bold) },
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
        ) {
            // Search Bar
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Search users by name or email...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = CircleShape,
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = SurfaceLight,
                    focusedContainerColor = Color.White,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent
                )
            )

            // User List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(mockUserList.filter { it.name.contains(searchQuery, ignoreCase = true) }) { user ->
                    UserListItem(user, onNavigateToUserDetail)
                }
            }
        }
    }
}

@Composable
fun UserListItem(user: UserSummary, onClick: (String) -> Unit) {
    ModernCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(user.id) },
        backgroundColor = SurfaceLight
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(user.name.take(1), fontWeight = FontWeight.Bold, fontSize = 20.sp)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(user.name, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text(user.email, fontSize = 12.sp, color = TextSecondary)
            }
            Column(horizontalAlignment = Alignment.End) {
                Surface(
                    shape = CircleShape,
                    color = if (user.role == "Premium") PrimaryPurple.copy(alpha = 0.1f) else Color.Gray.copy(alpha = 0.1f)
                ) {
                    Text(
                        user.role,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (user.role == "Premium") PrimaryPurple else TextSecondary
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(user.lastActive, fontSize = 11.sp, color = Color.Gray)
            }
        }
    }
}

data class UserSummary(val id: String, val name: String, val email: String, val role: String, val status: String, val lastActive: String)

val mockUserList = listOf(
    UserSummary("1", "John Smith", "john@email.com", "Premium", "Active", "2m ago"),
    UserSummary("2", "Maria Garcia", "maria@email.com", "Learner", "Active", "1h ago"),
    UserSummary("3", "Ahmed Hassan", "ahmed@email.com", "Learner", "Active", "3h ago"),
    UserSummary("4", "Li Wei", "li@email.com", "Premium", "Suspended", "2d ago"),
    UserSummary("5", "Sarah Johnson", "sarah@email.com", "Admin", "Active", "Now")
)
