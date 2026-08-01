package com.aiforall.app.presentation.screens.club

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aiforall.app.presentation.components.GlassCard

/**
 * Official AI Club hub: about, events, gallery, announcements, resources,
 * workshops, registration, membership, Hall of Fame, leaderboard,
 * winners, certificates — plus the entry point into the Community feed
 * (user posts about news/tools/theories) since that's fundamentally
 * club social content.
 */
@Composable
fun ClubScreen(onOpenCommunity: () -> Unit = {}) {
    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("AI Club", style = MaterialTheme.typography.headlineLarge)

        GlassCard(modifier = Modifier.fillMaxSize()) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Community Feed", style = MaterialTheme.typography.titleMedium)
                Text(
                    "See what members are posting — AI news, tools, and theories.",
                    style = MaterialTheme.typography.bodyMedium
                )
                Button(onClick = onOpenCommunity) { Text("Open Community") }
            }
        }
        // TODO next screens: events, gallery, announcements, leaderboard, certificates
    }
}
