package com.aiforall.app.presentation.screens.learn

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aiforall.app.presentation.components.GlassCard

private data class LearnTrack(val title: String, val level: String, val itemCount: Int)

// Seed content for the 5 Learn tracks from the spec (Prompt Engineering,
// Beginner/Intermediate/Advanced AI, AI Workflows). Tutorials/mini-courses/
// PDF notes/videos/prompt library become sub-screens off each track later.
private val tracks = listOf(
    LearnTrack("Prompt Engineering", "All levels", 12),
    LearnTrack("Beginner AI", "Beginner", 8),
    LearnTrack("Intermediate AI", "Intermediate", 10),
    LearnTrack("Advanced AI", "Advanced", 6),
    LearnTrack("AI Workflows", "All levels", 5)
)

@Composable
fun LearnScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
        Text("Learn", style = MaterialTheme.typography.headlineLarge)
        LazyColumn(
            modifier = Modifier.padding(top = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(tracks, key = { it.title }) { track ->
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(track.title, style = MaterialTheme.typography.titleMedium)
                        Text(
                            "${track.level} · ${track.itemCount} lessons",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}
