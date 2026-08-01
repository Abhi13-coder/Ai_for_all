package com.aiforall.app.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.aiforall.app.presentation.theme.GlassStroke

/**
 * Shared "glassmorphism" surface: translucent fill + soft gradient +
 * thin light-stroke border. Every card in the app (tool cards, news
 * cards, event cards) should be built on top of this rather than a
 * plain Material Card, to keep the frosted-glass look consistent.
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: androidx.compose.ui.unit.Dp = 20.dp,
    content: @Composable ColumnScopeAlias.() -> Unit
) {
    val cs = MaterialTheme.colorScheme
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        cs.surface.copy(alpha = 0.65f),
                        cs.surface.copy(alpha = 0.35f)
                    )
                )
            )
            .border(1.dp, GlassStroke, RoundedCornerShape(cornerRadius))
            .padding(16.dp)
    ) {
        content()
    }
}

// Type alias purely so the content lambda above reads cleanly as a
// Column scope without importing ColumnScope directly at call sites.
typealias ColumnScopeAlias = androidx.compose.foundation.layout.ColumnScope
