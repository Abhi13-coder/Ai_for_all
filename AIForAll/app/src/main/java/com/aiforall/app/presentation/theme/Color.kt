package com.aiforall.app.presentation.theme

import androidx.compose.ui.graphics.Color

// Dark-first palette. Deep near-black base (not pure #000 — keeps depth
// for glass/blur layers to read against) with electric blue/purple/cyan
// accents used for gradients, glows, and CTAs.

val SpaceBlack       = Color(0xFF0A0A0F)
val SurfaceElevated  = Color(0xFF14141C)
val GlassStroke      = Color(0x33FFFFFF) // 20% white, for glass card borders

val NeonBlue    = Color(0xFF4F7CFF)
val NeonPurple  = Color(0xFF9B5CFF)
val NeonCyan    = Color(0xFF3FE0D0)

val TextPrimary   = Color(0xFFF5F5F7)
val TextSecondary = Color(0xFFA0A0AC)

// Light mode (secondary priority)
val LightBackground  = Color(0xFFF7F7FA)
val LightSurface     = Color(0xFFFFFFFF)
val LightTextPrimary = Color(0xFF14141C)
