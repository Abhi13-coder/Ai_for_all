package com.aiforall.app.presentation.screens.community

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aiforall.app.domain.model.ClubRole
import com.aiforall.app.domain.model.ModerationStatus
import com.aiforall.app.domain.model.PostMediaType
import com.aiforall.app.domain.model.PostType
import com.aiforall.app.presentation.screens.auth.AuthViewModel

/**
 * Posting form. Publishes immediately — there's no approval queue —
 * so this screen carries the content-policy reminder directly, and
 * blocks submission client-side for RESTRICTED/BANNED accounts (the
 * real enforcement is server-side in firestore.rules; this is just
 * so a restricted user gets an immediate, clear reason instead of a
 * silent Firestore permission error).
 */
@Composable
fun CreatePostScreen(
    onPosted: () -> Unit,
    viewModel: CommunityViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val user by authViewModel.currentUser.collectAsState()
    val canPostEvents = user?.clubRole != null && user?.clubRole != ClubRole.NONE
    val canPostAtAll = user?.moderationStatus != ModerationStatus.RESTRICTED &&
        user?.moderationStatus != ModerationStatus.BANNED

    var title by remember { mutableStateOf("") }
    var body by remember { mutableStateOf("") }
    var linkUrl by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(PostType.DISCUSSION) }
    var imageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var videoUri by remember { mutableStateOf<Uri?>(null) }

    val pickImages = rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
        imageUris = uris.take(4) // cap at 4, like a typical X/Threads post
        videoUri = null
    }
    val pickVideo = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        videoUri = uri
        imageUris = emptyList()
    }

    val availableTypes = PostType.entries.filter { it != PostType.EVENT || canPostEvents }

    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("New Post", style = MaterialTheme.typography.headlineMedium)
        Text(
            "Keep it AI or tech related — no sexual content. Off-topic or " +
                "inappropriate posts can lead to a warning, posting restriction, " +
                "shadowban, or permanent ban.",
            style = MaterialTheme.typography.labelSmall
        )

        if (!canPostAtAll) {
            Text(
                "Your account is currently ${user?.moderationStatus?.name?.lowercase()} and can't post right now.",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        } else {
            Column {
                Text("What are you sharing?", style = MaterialTheme.typography.labelSmall)
                availableTypes.forEach { type ->
                    Button(
                        onClick = { selectedType = type },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text(type.name + if (type == selectedType) " ✓" else "") }
                }
                if (!canPostEvents) {
                    Text(
                        "Redeem a club code in Profile to post official Events.",
                        style = MaterialTheme.typography.labelSmall
                    )
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

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { pickImages.launch("image/*") }) {
                    Text(if (imageUris.isEmpty()) "Add photos" else "${imageUris.size} photo(s) selected")
                }
                OutlinedButton(onClick = { pickVideo.launch("video/*") }) {
                    Text(videoUri?.let { "1 video selected" } ?: "Add video")
                }
            }

            Button(
                onClick = {
                    val current = user
                    if (current != null) {
                        val (mediaUris, mediaType) = when {
                            videoUri != null -> listOf(videoUri!!) to PostMediaType.VIDEO
                            imageUris.isNotEmpty() -> imageUris to PostMediaType.IMAGE
                            else -> emptyList<Uri>() to PostMediaType.NONE
                        }
                        viewModel.submitPost(
                            title = title,
                            body = body,
                            type = if (selectedType == PostType.EVENT && !canPostEvents) PostType.DISCUSSION else selectedType,
                            linkUrl = linkUrl.ifBlank { null },
                            authorId = current.uid,
                            authorName = current.displayName,
                            mediaUris = mediaUris,
                            mediaType = mediaType
                        )
                        onPosted()
                    }
                },
                enabled = user != null && title.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) { Text("Post") }
        }
    }
}
