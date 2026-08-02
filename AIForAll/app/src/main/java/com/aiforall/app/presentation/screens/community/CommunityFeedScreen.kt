package com.aiforall.app.presentation.screens.community

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.aiforall.app.domain.model.Post
import com.aiforall.app.domain.model.PostMediaType
import com.aiforall.app.presentation.components.GlassCard
import com.aiforall.app.presentation.screens.auth.AuthViewModel

/**
 * Main social feed — text + optional photos/video per post, X/Threads
 * style (no vertical video-only reel layout). Backed by
 * CommunityViewModel, which already filters out hidden/banned/
 * shadowbanned content per-viewer. FAB opens CreatePostScreen.
 */
@Composable
fun CommunityFeedScreen(
    onCreatePost: () -> Unit,
    viewModel: CommunityViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val posts by viewModel.posts.collectAsState()
    val user by authViewModel.currentUser.collectAsState()

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
                items(posts, key = { it.id }) { post ->
                    PostCard(
                        post = post,
                        onReport = { user?.let { viewModel.reportPost(post.id, it.uid, "reported from feed") } }
                    )
                }
            }
        }
    }
}

@Composable
private fun PostCard(post: Post, onReport: () -> Unit) {
    val context = LocalContext.current

    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(post.type.name, style = MaterialTheme.typography.labelSmall)
                Text("· ${post.authorName}", style = MaterialTheme.typography.labelSmall)
            }
            Text(post.title, style = MaterialTheme.typography.titleMedium)
            Text(post.body, style = MaterialTheme.typography.bodyMedium)

            when (post.mediaType) {
                PostMediaType.IMAGE -> {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(post.mediaUrls) { url ->
                            AsyncImage(
                                model = url,
                                contentDescription = null,
                                modifier = Modifier
                                    .height(180.dp)
                                    .clip(RoundedCornerShape(12.dp))
                            )
                        }
                    }
                }
                PostMediaType.VIDEO -> {
                    val videoUrl = post.mediaUrls.firstOrNull()
                    if (videoUrl != null) {
                        TextButton(onClick = {
                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl)))
                        }) { Text("▶ Play video") }
                    }
                }
                PostMediaType.NONE -> Unit
            }

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("♥ ${post.likeCount}", style = MaterialTheme.typography.labelSmall)
                Text("💬 ${post.commentCount}", style = MaterialTheme.typography.labelSmall)
                TextButton(onClick = onReport) { Text("Report", style = MaterialTheme.typography.labelSmall) }
            }
        }
    }
}
