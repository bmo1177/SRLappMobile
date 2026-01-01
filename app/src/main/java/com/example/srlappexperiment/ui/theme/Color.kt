package com.example.srlappexperiment.ui.theme

import androidx.compose.ui.graphics.Color

// Primary color palette - Modern Purples and Blues
val PrimaryPurple = Color(0xFF7C4DFF) // More vibrant purple
val PrimaryBlue = Color(0xFF00A8FF) // More vibrant blue
val PrimaryLight = Color(0xFFB388FF)
val PrimaryDark = Color(0xFF6200EA)
val PrimaryTeal = Color(0xFF00D2D3)

// Accent color palette - Vibrant Corals and Pinks
val AccentCoral = Color(0xFFFF6B6B)
val AccentPink = Color(0xFFFF9FF3)
val AccentLight = Color(0xFFFFEAA7)

// Background and Surface
val BackgroundLight = Color(0xFFF0F2F5) // Soft light grey
val BackgroundDark = Color(0xFF0F172A) // Slate dark
val SurfaceLight = Color(0xFFFFFFFF)
val SurfaceDark = Color(0xFF1E293B)

// Text colors
val TextPrimary = Color(0xFF1E293B)
val TextSecondary = Color(0xFF64748B)
val TextOnDark = Color(0xFFF8FAFC)

// Semantic colors
val Success = Color(0xFF10B981)
val Error = Color(0xFFEF4444)
val Warning = Color(0xFFF59E0B)
val Info = Color(0xFF3B82F6)

// Gamification colors
val GoldAccent = Color(0xFFF59E0B)
val SilverAccent = Color(0xFF94A3B8)
val BronzeAccent = Color(0xFFD97706)

// Backward compatibility (Mapping old names to new palette)
val TealPrimary = PrimaryPurple
val TealLight = PrimaryLight
val TealDark = PrimaryDark
val CreamPrimary = BackgroundLight
val CreamLight = SurfaceLight
val CreamDark = Color(0xFFE2E8F0)
val CharcoalPrimary = TextPrimary
val CharcoalLight = TextSecondary
val CharcoalDark = BackgroundDark

// Missing colors from previous theme (backward compatibility)
val SecondaryOrange = AccentCoral
val SecondaryOrangeVariant = AccentPink