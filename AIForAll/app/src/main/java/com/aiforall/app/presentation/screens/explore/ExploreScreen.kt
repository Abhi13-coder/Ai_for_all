package com.aiforall.app.presentation.screens.explore

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aiforall.app.presentation.components.GlassCard

/**
 * Search bar + category filter chips + tool cards. Backed by
 * `sampleTools` for now (see ToolCategory.kt) — swap for a real
 * repository once curated tool data exists; layout/filtering logic
 * doesn't change either way.
 */
@Composable
fun ExploreScreen() {
    var query by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<ToolCategory?>(null) }

    val filtered = sampleTools.filter { tool ->
        (selectedCategory == null || tool.category == selectedCategory) &&
            (query.isBlank() || tool.name.contains(query, ignoreCase = true))
    }

    Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
        Text("Explore", style = MaterialTheme.typography.headlineLarge)

        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            placeholder = { Text("Search AI tools") },
            leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
        )

        LazyRow(
            modifier = Modifier.padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(ToolCategory.entries) { category ->
                FilterChip(
                    selected = selectedCategory == category,
                    onClick = { selectedCategory = if (selectedCategory == category) null else category },
                    label = { Text(category.label) }
                )
            }
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            items(filtered, key = { it.name }) { tool -> ToolCard(tool) }
        }
    }
}

@Composable
private fun ToolCard(tool: AiTool) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(tool.name, style = MaterialTheme.typography.titleMedium)
                Text("★ ${tool.rating}", style = MaterialTheme.typography.labelSmall)
            }
            Text(tool.description, style = MaterialTheme.typography.bodyMedium)
            Text(tool.website, style = MaterialTheme.typography.labelSmall)
            Text(tool.pricing, style = MaterialTheme.typography.labelSmall)
            Text("+ " + tool.pros.joinToString(), style = MaterialTheme.typography.labelSmall)
            Text("- " + tool.cons.joinToString(), style = MaterialTheme.typography.labelSmall)
        }
    }
}
