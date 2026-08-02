package com.aiforall.app.presentation.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aiforall.app.presentation.components.GlassCard
import com.aiforall.app.presentation.screens.auth.AuthViewModel

/**
 * Profile hub: identity + real membership tier/club role at top (from
 * the signed-in UserProfile, not a placeholder), club-code redemption,
 * then section cards, then sign out.
 */
@Composable
fun ProfileScreen(authViewModel: AuthViewModel = hiltViewModel()) {
    val user by authViewModel.currentUser.collectAsState()
    var codeInput by remember { mutableStateOf("") }
    var redeemMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
            )
            Column(modifier = Modifier.padding(start = 16.dp)) {
                Text(user?.displayName ?: "—", style = MaterialTheme.typography.headlineMedium)
                Text(
                    "${user?.membershipTier?.name ?: "PUBLIC"} · ${user?.clubRole?.name ?: "NONE"}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Club Code", style = MaterialTheme.typography.titleMedium)
                Text(
                    "Verified members can redeem a club code here to unlock posting official Events and other member privileges.",
                    style = MaterialTheme.typography.bodyMedium
                )
                OutlinedTextField(
                    value = codeInput,
                    onValueChange = { codeInput = it },
                    label = { Text("Enter code") },
                    modifier = Modifier.fillMaxWidth()
                )
                Button(
                    onClick = {
                        authViewModel.redeemClubCode(codeInput) { result ->
                            redeemMessage = result.fold(
                                onSuccess = { "Code redeemed — welcome to the club." },
                                onFailure = { it.message ?: "That code didn't work." }
                            )
                        }
                    },
                    enabled = codeInput.isNotBlank(),
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Redeem") }
                redeemMessage?.let { Text(it, style = MaterialTheme.typography.labelSmall) }
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

        TextButton(onClick = { authViewModel.signOut() }) { Text("Sign out") }
    }
}
