package com.aiforall.app.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush

/**
 * Full-screen radial/linear gradient wash used behind every screen so the
 * app never shows a flat single-color background. One shared composable
 * keeps the "premium" gradient language consistent app-wide.
 */
@Composable
fun GradientBackground(content: @Composable () -> Unit) {
    val cs = MaterialTheme.colorScheme
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        cs.background,
                        cs.background,
                        cs.primary.copy(alpha = 0.10f)
                    )
                )
            )
    ) {
        content()
    }
}
