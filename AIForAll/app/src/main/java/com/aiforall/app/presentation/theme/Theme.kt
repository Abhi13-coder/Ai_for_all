package com.aiforall.app.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColors = darkColorScheme(
    primary = NeonBlue,
    secondary = NeonPurple,
    tertiary = NeonCyan,
    background = SpaceBlack,
    surface = SurfaceElevated,
    onBackground = TextPrimary,
    onSurface = TextPrimary
)

private val LightColors = lightColorScheme(
    primary = NeonBlue,
    secondary = NeonPurple,
    tertiary = NeonCyan,
    background = LightBackground,
    surface = LightSurface,
    onBackground = LightTextPrimary,
    onSurface = LightTextPrimary
)

@Composable
fun AIForAllTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colors,
        typography = AIForAllTypography,
        content = content
    )
}
