package com.aiforall.app.presentation.screens.community

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.aiforall.app.domain.model.Post
import com.aiforall.app.presentation.components.GlassCard

/**
 * Public feed of APPROVED community posts (news/tool/theory/discussion
 * shared by members). Backed by CommunityViewModel -> PostRepository ->
 * Firestore. FAB opens CreatePostScreen for the "let users post" feature.
 */
@Composable
fun CommunityFeedScreen(
    onCreatePost: () -> Unit,
    viewModel: CommunityViewModel = hiltViewModel()
) {
    val posts by viewModel.posts.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onCreatePost) {
                Icon(Icons.Filled.Add, contentDescription = "New post")
            }
        }
    ) { padding ->
        if (posts.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No posts yet — be the first to share something.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(posts, key = { it.id }) { post -> PostCard(post) }
            }
        }
    }
}

@Composable
private fun PostCard(post: Post) {
    GlassCard(modifier = Modifier.fillMaxSize()) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(post.type.name, style = MaterialTheme.typography.labelSmall)
                Text("· ${post.authorName}", style = MaterialTheme.typography.labelSmall)
            }
            Text(post.title, style = MaterialTheme.typography.titleMedium)
            Text(post.body, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
