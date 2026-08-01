package com.aiforall.app.presentation.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aiforall.app.presentation.components.GlassCard

/**
 * Home = greeting + featured/news/research/events cards + quick actions
 * (including "+ Post to Community", the new feature) + premium banner.
 * Sample content for now; swap each section for a repository-backed
 * list once curated content pipelines exist.
 */
@Composable
fun HomeScreen(onOpenCreatePost: () -> Unit = {}) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Text("Good to see you 👋", style = MaterialTheme.typography.headlineLarge) }

        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Trending", style = MaterialTheme.typography.labelSmall)
                    Text("Anthropic ships Claude Sonnet 5", style = MaterialTheme.typography.titleMedium)
                }
            }
        }

        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Featured Tool", style = MaterialTheme.typography.labelSmall)
                    Text("Claude — reasoning & coding assistant", style = MaterialTheme.typography.titleMedium)
                }
            }
        }

        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Event, contentDescription = null)
                    Column(modifier = Modifier.padding(start = 12.dp)) {
                        Text("Upcoming Event", style = MaterialTheme.typography.labelSmall)
                        Text("AI Club Workshop — Prompt Engineering 101", style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }

        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Quick Actions", style = MaterialTheme.typography.titleMedium)
                    Button(onClick = onOpenCreatePost) {
                        Icon(Icons.Outlined.Add, contentDescription = null)
                        Text("  Post to Community")
                    }
                }
            }
        }

        items(remainingSections) { section ->
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text(section, style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

private val remainingSections = listOf(
    "Today's AI News", "Recommended Tools", "Latest Research",
    "Featured Article", "Premium Banner", "Recent Activity"
)
