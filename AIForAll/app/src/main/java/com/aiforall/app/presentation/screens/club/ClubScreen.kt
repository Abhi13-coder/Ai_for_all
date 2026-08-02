package com.aiforall.app.presentation.screens.club

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aiforall.app.domain.model.ClubRole
import com.aiforall.app.presentation.components.GlassCard
import com.aiforall.app.presentation.screens.auth.AuthViewModel

/**
 * Official AI Club hub: about, events, gallery, announcements, resources,
 * workshops, registration, membership, Hall of Fame, leaderboard,
 * winners, certificates — plus the entry point into the Community feed,
 * and (ADMIN only) the moderation panel.
 */
@Composable
fun ClubScreen(
    onOpenCommunity: () -> Unit = {},
    onOpenModeration: () -> Unit = {},
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val user by authViewModel.currentUser.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("AI Club", style = MaterialTheme.typography.headlineLarge)

        GlassCard(modifier = Modifier.fillMaxSize()) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Community Feed", style = MaterialTheme.typography.titleMedium)
                Text(
                    "See what members are posting — AI news, tools, theories, and events.",
                    style = MaterialTheme.typography.bodyMedium
                )
                Button(onClick = onOpenCommunity) { Text("Open Community") }
            }
        }

        if (user?.clubRole == ClubRole.ADMIN) {
            GlassCard(modifier = Modifier.fillMaxSize()) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Moderation", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "Review reported posts and manage member standing.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Button(onClick = onOpenModeration) { Text("Open Moderation Panel") }
                }
            }
        }
        // TODO next screens: gallery, announcements, leaderboard, certificates
    }
}
