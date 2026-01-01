package com.example.srlappexperiment.presentation.ui.screens.settings

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.srlappexperiment.presentation.viewmodel.settings.SettingsUiState
import com.example.srlappexperiment.presentation.viewmodel.settings.SettingsViewModel
import com.example.srlappexperiment.presentation.viewmodel.auth.AuthViewModel
import com.example.srlappexperiment.presentation.ui.components.*
import com.example.srlappexperiment.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onNavigateToAdmin: () -> Unit,
    onNavigateToSupport: () -> Unit,
    onNavigateToSubscription: () -> Unit,
    onLogout: () -> Unit
) {
    val prefs by viewModel.userPreferences.collectAsState()
    val notifs by viewModel.notificationSettings.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()
    
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Check if user is admin - check email for admin accounts
    val isAdmin = remember(currentUser?.email) {
        currentUser?.email?.lowercase()?.contains("admin") == true ||
        currentUser?.email?.lowercase() == "admin@test.com"
    }
    
    // Dialog states
    var showNameDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }
    
    var tempName by remember { mutableStateOf(prefs.displayName) }
    LaunchedEffect(prefs.displayName) { tempName = prefs.displayName }

    // Handle UI State changes
    LaunchedEffect(uiState) {
        when (uiState) {
            is SettingsUiState.Success -> {
                snackbarHostState.showSnackbar((uiState as SettingsUiState.Success).message)
                viewModel.resetUiState()
            }
            is SettingsUiState.Error -> {
                snackbarHostState.showSnackbar((uiState as SettingsUiState.Error).message)
                viewModel.resetUiState()
            }
            is SettingsUiState.LoggedOut -> {
                onLogout()
            }
            is SettingsUiState.ExportSuccess -> {
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "application/json"
                    putExtra(Intent.EXTRA_STREAM, (uiState as SettingsUiState.ExportSuccess).uri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                context.startActivity(Intent.createChooser(intent, "Export Data"))
                viewModel.resetUiState()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Settings", 
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold 
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundLight)
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
            // Profile Card (Header)
            ModernCard(
                backgroundColor = Color.Transparent, // Will use brush background
                shape = RoundedCornerShape(28.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Brush.linearGradient(listOf(PrimaryPurple, PrimaryBlue)))
                        .padding(24.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(72.dp),
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(2.dp, Color.White.copy(alpha = 0.4f))
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = prefs.displayName.firstOrNull()?.toString()?.uppercase() ?: "U",
                                    style = MaterialTheme.typography.displaySmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                            }
                        }
                        Spacer(Modifier.width(20.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = prefs.displayName,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Lvl 12",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(modifier = Modifier.size(4.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.6f)))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Language Learner",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                            }
                        }
                        IconButton(
                            onClick = { showNameDialog = true },
                            modifier = Modifier.clip(CircleShape).background(Color.White.copy(alpha = 0.1f))
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Name", tint = Color.White)
                        }
                    }
                }
            }
            
            // Admin Section (Visible to Admin Role only)
            // Note: To enable admin access, set user role to "admin" in Firestore
            // or modify the isAdmin check above to read from user repository
            if (isAdmin) {
                SettingsSection(title = "Administrative Center") {
                    SettingItem(
                        icon = Icons.Default.AdminPanelSettings,
                        label = "Admin Dashboard",
                        value = "Platform metrics & user management",
                        onClick = onNavigateToAdmin
                    )
                }
            }

            // Subscription Section
            SettingsSection(title = "Subscription") {
                SettingItem(
                    icon = Icons.Default.WorkspacePremium,
                    label = "Premium Status",
                    value = if (prefs.isPremium) "Pro Active" else "Switch to Pro",
                    onClick = onNavigateToSubscription
                )
            }

            // Account Section
            SettingsSection(title = "Account") {
                SettingItem(
                    icon = Icons.Default.Email,
                    label = "Email Address",
                    value = "user@example.com",
                    isEditable = false
                )
                SettingItem(
                    icon = Icons.Default.Security,
                    label = "Change Password",
                    value = "Last changed 3 months ago",
                    onClick = { /* Navigate to password change */ }
                )
            }

            // Learning Preferences
            SettingsSection(title = "Learning") {
                SliderSetting(
                    label = "Daily Study Goal",
                    value = prefs.dailyGoalMinutes,
                    valueRange = 15f..60f,
                    unit = "min",
                    onValueChange = { viewModel.updatePreferences(prefs.copy(dailyGoalMinutes = it.toInt())) }
                )
                SliderSetting(
                    label = "New Cards Per Day",
                    value = prefs.newCardsPerDay,
                    valueRange = 5f..20f,
                    unit = "",
                    onValueChange = { viewModel.updatePreferences(prefs.copy(newCardsPerDay = it.toInt())) }
                )
                SettingItem(
                    icon = Icons.Default.Language,
                    label = "Target Language",
                    value = prefs.targetLanguage,
                    onClick = { showLanguageDialog = true }
                )
            }

            // Notifications
            SettingsSection(title = "Notifications") {
                SwitchSetting(
                    icon = Icons.Default.Notifications,
                    label = "Learning Reminders",
                    checked = notifs.enabled,
                    onCheckedChange = { viewModel.updateNotificationSettings(notifs.copy(enabled = it)) }
                )
                if (notifs.enabled) {
                    SettingItem(
                        icon = Icons.Default.AccessTime,
                        label = "Quiet Hours",
                        value = "${notifs.quietHoursStart} - ${notifs.quietHoursEnd}",
                        onClick = { showStartTimePicker = true }
                    )
                }
            }

            // App Settings
            SettingsSection(title = "App Settings") {
                SwitchSetting(
                    icon = Icons.Default.DarkMode,
                    label = "Dark Mode",
                    checked = prefs.isDarkMode,
                    onCheckedChange = { viewModel.toggleDarkMode() }
                )
                SwitchSetting(
                    icon = Icons.Default.AutoAwesome,
                    label = "Simplified Mode (No Animations)",
                    checked = false, // Mock state
                    onCheckedChange = { /* viewModel.toggleSimplifiedMode() */ }
                )
                SettingItem(
                    icon = Icons.Default.Download,
                    label = "Export My Data",
                    value = "Download in JSON Format",
                    onClick = { viewModel.exportUserData() }
                )
                SettingItem(
                    icon = Icons.Default.DeleteForever,
                    label = "Delete Account",
                    value = "Permanently remove your data",
                    textColor = com.example.srlappexperiment.ui.theme.Error,
                    onClick = { showDeleteDialog = true }
                )
            }

            // Support & Feedback
            SettingsSection(title = "Support & Feedback") {
                SettingItem(
                    icon = Icons.Default.HelpCenter,
                    label = "Help Center",
                    value = "FAQs and support tickets",
                    onClick = onNavigateToSupport
                )
                SettingItem(
                    icon = Icons.Default.Chat,
                    label = "Send Feedback",
                    value = "Tell us what you think",
                    onClick = { /* Feedback dialog */ }
                )
            }

            Spacer(Modifier.height(16.dp))
            
            GradientButton(
                text = "Logout",
                onClick = { viewModel.logout() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = listOf(AccentCoral, Color(0xFFFF4757))
            )
            
            Spacer(Modifier.height(16.dp))
            
            Text(
                "Version 2.0.0 (SRL Mastery)",
                modifier = Modifier.align(Alignment.CenterHorizontally),
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
            
            Spacer(Modifier.height(32.dp))
        }

        // Dialogs
        if (showNameDialog) {
            AlertDialog(
                onDismissRequest = { showNameDialog = false },
                title = { Text("Edit Display Name") },
                text = {
                    OutlinedTextField(
                        value = tempName,
                        onValueChange = { tempName = it },
                        label = { Text("Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryPurple,
                            focusedLabelColor = PrimaryPurple
                        )
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.updatePreferences(prefs.copy(displayName = tempName))
                        showNameDialog = false
                    }) { Text("Save", color = PrimaryPurple) }
                },
                dismissButton = {
                    TextButton(onClick = { showNameDialog = false }) { Text("Cancel", color = TextSecondary) }
                }
            )
        }

        if (showLanguageDialog) {
            AlertDialog(
                onDismissRequest = { showLanguageDialog = false },
                title = { Text("Select Target Language") },
                text = {
                    Column {
                        LanguageOption("English", prefs.targetLanguage == "English") { 
                            viewModel.updatePreferences(prefs.copy(targetLanguage = "English"))
                            showLanguageDialog = false 
                        }
                        LanguageOption("Spanish", prefs.targetLanguage == "Spanish") { 
                            viewModel.updatePreferences(prefs.copy(targetLanguage = "Spanish"))
                            showLanguageDialog = false 
                        }
                    }
                },
                confirmButton = { TextButton(onClick = { showLanguageDialog = false }) { Text("Close", color = PrimaryPurple) } }
            )
        }

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Delete Account") },
                text = { Text("Are you sure you want to permanently delete your account? This action cannot be undone.") },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.deleteAccount()
                        showDeleteDialog = false
                    }) { Text("Delete", color = com.example.srlappexperiment.ui.theme.Error) }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel", color = TextSecondary) }
                }
            )
        }

        if (showStartTimePicker) {
            TimePickerModal(
                onDismiss = { showStartTimePicker = false },
                onConfirm = { hour, minute ->
                    val timeString = String.format("%02d:%02d", hour, minute)
                    viewModel.updateNotificationSettings(notifs.copy(quietHoursStart = timeString))
                    showStartTimePicker = false
                    showEndTimePicker = true // Chain to end time
                }
            )
        }

        if (showEndTimePicker) {
            TimePickerModal(
                onDismiss = { showEndTimePicker = false },
                onConfirm = { hour, minute ->
                    val timeString = String.format("%02d:%02d", hour, minute)
                    viewModel.updateNotificationSettings(notifs.copy(quietHoursEnd = timeString))
                    showEndTimePicker = false
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerModal(
    onDismiss: () -> Unit,
    onConfirm: (Int, Int) -> Unit
) {
    val timePickerState = rememberTimePickerState()

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = { onConfirm(timePickerState.hour, timePickerState.minute) }) {
                Text("OK", color = PrimaryPurple)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        },
        text = {
            TimePicker(state = timePickerState)
        }
    )
}

@Composable
fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary,
            fontWeight = FontWeight.Bold
        )
        ModernCard(
            backgroundColor = SurfaceLight,
            shape = RoundedCornerShape(20.dp),
            content = content
        )
    }
}

@Composable
fun SettingItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    isEditable: Boolean = true,
    textColor: Color = TextPrimary,
    onClick: (() -> Unit)? = null
) {
    Surface(
        onClick = { onClick?.invoke() },
        enabled = onClick != null,
        modifier = Modifier.fillMaxWidth(),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (textColor == com.example.srlappexperiment.ui.theme.Error) com.example.srlappexperiment.ui.theme.Error.copy(alpha = 0.1f) else PrimaryPurple.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (textColor == com.example.srlappexperiment.ui.theme.Error) com.example.srlappexperiment.ui.theme.Error else PrimaryPurple,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(label, style = MaterialTheme.typography.titleMedium, color = textColor, fontWeight = FontWeight.SemiBold)
                if (value.isNotEmpty()) {
                    Text(value, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                }
            }
            if (isEditable && onClick != null) {
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = SilverAccent)
            }
        }
    }
}

@Composable
fun SliderSetting(
    label: String,
    value: Int,
    valueRange: ClosedFloatingPointRange<Float>,
    unit: String,
    onValueChange: (Float) -> Unit
) {
    Column(modifier = Modifier.padding(20.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            Text("$value $unit", fontWeight = FontWeight.Bold, color = PrimaryPurple)
        }
        Slider(
            value = value.toFloat(),
            onValueChange = onValueChange,
            valueRange = valueRange,
            colors = SliderDefaults.colors(
                thumbColor = PrimaryPurple,
                activeTrackColor = PrimaryPurple,
                inactiveTrackColor = SilverAccent.copy(alpha = 0.3f)
            )
        )
    }
}

@Composable
fun SwitchSetting(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(PrimaryPurple.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = PrimaryPurple,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(Modifier.width(16.dp))
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = PrimaryPurple,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = SilverAccent.copy(alpha = 0.5f)
            )
        )
    }
}

@Composable
fun LanguageOption(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = isSelected, onClick = onClick, colors = RadioButtonDefaults.colors(selectedColor = PrimaryPurple))
        Spacer(modifier = Modifier.width(12.dp))
        Text(label, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
    }
}
