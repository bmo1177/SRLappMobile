package com.example.srlappexperiment.presentation.ui.screens.billing

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.srlappexperiment.ui.theme.*
import com.example.srlappexperiment.presentation.ui.components.ModernCard
import com.example.srlappexperiment.presentation.ui.components.GradientButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionScreen(
    onNavigateBack: () -> Unit
) {
    var selectedPlan by remember { mutableIntStateOf(1) } // 0: Monthly, 1: Annual

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Go Premium", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(PrimaryPurple, PrimaryBlue))),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Star, contentDescription = null, tint = Color.White, modifier = Modifier.size(40.dp))
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                "Unlock Your Full Potential",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )
            
            Text(
                "Master a new language 3x faster with AI",
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Plan Selection
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                PlanCard(
                    title = "Monthly",
                    price = "$9.99",
                    period = "/mo",
                    isSelected = selectedPlan == 0,
                    onClick = { selectedPlan = 0 },
                    modifier = Modifier.weight(1f)
                )
                PlanCard(
                    title = "Annual",
                    price = "$4.99",
                    period = "/mo",
                    tag = "SAVE 50%",
                    isSelected = selectedPlan == 1,
                    onClick = { selectedPlan = 1 },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Benefits
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                BenefitItem("Unlimited AI Conversation Partner")
                BenefitItem("Advanced Grammar Analysis")
                BenefitItem("Offline Learning Mode")
                BenefitItem("Ad-Free Experience")
                BenefitItem("Priority Support")
            }

            Spacer(modifier = Modifier.height(48.dp))

            GradientButton(
                text = "Continue to Payment",
                onClick = { /* Mock Payment */ },
                modifier = Modifier.fillMaxWidth().height(56.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                "Cancel anytime. Secure payment handled by Google Play.",
                fontSize = 12.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun PlanCard(title: String, price: String, period: String, tag: String? = null, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier) {
    ModernCard(
        modifier = modifier
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) PrimaryPurple else Color.LightGray.copy(alpha = 0.3f),
                shape = RoundedCornerShape(20.dp)
            )
            .clickable { onClick() },
        backgroundColor = if (isSelected) PrimaryPurple.copy(alpha = 0.05f) else SurfaceLight
    ) {
        Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            if (tag != null) {
                Surface(
                    color = PrimaryPurple,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(tag, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 10.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
            Text(title, fontWeight = FontWeight.Bold, color = if (isSelected) PrimaryPurple else TextPrimary)
            Row(verticalAlignment = Alignment.Bottom) {
                Text(price, fontWeight = FontWeight.ExtraBold, fontSize = 24.sp)
                Text(period, fontSize = 12.sp, color = TextSecondary)
            }
        }
    }
}

@Composable
fun BenefitItem(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier.size(24.dp).clip(CircleShape).background(Success.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp), tint = Success)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(text, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
    }
}
