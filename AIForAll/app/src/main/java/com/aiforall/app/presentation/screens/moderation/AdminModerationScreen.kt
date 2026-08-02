package com.aiforall.app.presentation.screens.moderation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aiforall.app.domain.model.ModerationStatus
import com.aiforall.app.domain.model.Post
import com.aiforall.app.presentation.components.GlassCard

/**
 * ADMIN-only screen (gated in NavGraph, not just hidden — see
 * firestore.rules for the actual server-side enforcement). Everything
 * here is an action a club admin used to have to do by hand in the
 * Firebase console; now it's all in-app since console access isn't
 * assumed.
 */
@Composable
fun AdminModerationScreen(viewModel: ModerationViewModel = hiltViewModel()) {
    val reportedPosts by viewModel.reportedPosts.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
        Text("Reported Posts", style = MaterialTheme.typography.headlineLarge)
        Text(
            "Escalate bumps the author one step: Warned → Restricted → Shadowbanned → Banned.",
            style = MaterialTheme.typography.labelSmall
        )

        if (reportedPosts.isEmpty()) {
            Text("Nothing reported right now.", modifier = Modifier.padding(top = 16.dp))
        } else {
            LazyColumn(
                modifier = Modifier.padding(top = 12.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(reportedPosts, key = { it.id }) { post ->
                    ReportedPostCard(
                        post = post,
                        onRemovePost = { viewModel.removePost(post.id) },
                        onDismiss = { viewModel.dismissReports(post.id) },
                        onEscalate = { viewModel.escalateAuthor(post.authorId) },
                        onBanDirectly = { viewModel.setAuthorStatus(post.authorId, ModerationStatus.BANNED) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ReportedPostCard(
    post: Post,
    onRemovePost: () -> Unit,
    onDismiss: () -> Unit,
    onEscalate: () -> Unit,
    onBanDirectly: () -> Unit
) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("${post.reportCount} report(s) · by ${post.authorName}", style = MaterialTheme.typography.labelSmall)
            Text(post.title, style = MaterialTheme.typography.titleMedium)
            Text(post.body, style = MaterialTheme.typography.bodyMedium)

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onDismiss) { Text("Dismiss") }
                OutlinedButton(onClick = onRemovePost) { Text("Remove post") }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onEscalate) { Text("Escalate author") }
                Button(onClick = onBanDirectly) { Text("Ban directly") }
            }
        }
    }
}
