package com.aiforall.app.presentation.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.background
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.aiforall.app.presentation.components.GlassCard

/**
 * Profile hub: identity + membership tier at top, then a card per
 * section (badges, certificates, events, achievements, settings).
 * All static placeholders for now — real data comes from Firebase
 * Auth + Firestore user doc once auth is wired in.
 */
@Composable
fun ProfileScreen() {
    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
            )
            Column(modifier = Modifier.padding(start = 16.dp)) {
                Text("Your Name", style = MaterialTheme.typography.headlineMedium)
                Text("Normal User", style = MaterialTheme.typography.bodyMedium)
            }
        }

        listOf(
            "Badges" to "Earned through events and achievements",
            "Certificates" to "Workshop and event completion certificates",
            "Events Attended" to "Your AI Club event history",
            "Achievements" to "Milestones and Hall of Fame entries",
            "Downloads" to "Saved PDF notes and offline content",
            "Bookmarks" to "Tools and articles you've saved",
            "Settings" to "Dark mode, notifications, privacy, security"
        ).forEach { (title, subtitle) ->
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text(title, style = MaterialTheme.typography.titleMedium)
                    Text(subtitle, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}
