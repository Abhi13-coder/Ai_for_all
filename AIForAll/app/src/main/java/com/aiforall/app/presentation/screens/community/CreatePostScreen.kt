package com.aiforall.app.presentation.screens.community

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aiforall.app.domain.model.PostType

/**
 * Form for the "let users post AI news / tools / theories" feature.
 * Type selector lets the feed later filter/tag community content the
 * same way curated Explore/News content is tagged. Submitted posts land
 * as PENDING_REVIEW server-side (see PostRepositoryImpl) — this screen
 * doesn't need to know about moderation, it just calls submitPost.
 */
@Composable
fun CreatePostScreen(
    onPosted: () -> Unit,
    viewModel: CommunityViewModel = hiltViewModel()
) {
    var title by remember { mutableStateOf("") }
    var body by remember { mutableStateOf("") }
    var linkUrl by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(PostType.DISCUSSION) }

    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("New Post", style = MaterialTheme.typography.headlineMedium)

        // Type chips — simple Row of text buttons for now, swap for
        // FilterChip once the shared chip component exists.
        Column {
            Text("What are you sharing?", style = MaterialTheme.typography.labelSmall)
            PostType.entries.forEach { type ->
                Button(
                    onClick = { selectedType = type },
                    modifier = Modifier.fillMaxWidth()
                ) { Text(type.name + if (type == selectedType) " ✓" else "") }
            }
        }

        OutlinedTextField(
            value = title, onValueChange = { title = it },
            label = { Text("Title") }, modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = body, onValueChange = { body = it },
            label = { Text("Details") }, modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = linkUrl, onValueChange = { linkUrl = it },
            label = { Text("Source / tool link (optional)") }, modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                viewModel.submitPost(
                    title = title,
                    body = body,
                    type = selectedType,
                    linkUrl = linkUrl.ifBlank { null },
                    authorId = "current_user_id", // TODO wire to Firebase Auth uid
                    authorName = "You"
                )
                onPosted()
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Submit for review") }
    }
}
