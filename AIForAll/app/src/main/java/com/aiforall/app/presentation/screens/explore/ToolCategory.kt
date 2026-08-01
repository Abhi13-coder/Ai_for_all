package com.aiforall.app.presentation.screens.explore

/** The 9 category chips from the Explore spec. */
enum class ToolCategory(val label: String) {
    CHAT("Chat AI"), IMAGE("Image AI"), VIDEO("Video AI"), CODING("Coding AI"),
    MUSIC("Music AI"), VOICE("Voice AI"), RESEARCH("Research AI"),
    PRODUCTIVITY("Productivity AI"), EDUCATION("Education AI")
}

/** Shape all tool cards must fill, per the spec: logo, description, website, pricing, rating, pros, cons. */
data class AiTool(
    val name: String,
    val category: ToolCategory,
    val description: String,
    val website: String,
    val pricing: String,
    val rating: Float,
    val pros: List<String>,
    val cons: List<String>
)

// Sample seed data so Explore renders real cards immediately. Replace
// with a Firestore-backed `ToolRepository` once curated data is ready —
// the UI below doesn't care where the list comes from.
val sampleTools = listOf(
    AiTool(
        "Claude", ToolCategory.CHAT,
        "Conversational AI assistant for writing, coding, and analysis.",
        "claude.ai", "Free tier + paid plans", 4.8f,
        listOf("Strong reasoning", "Long context"), listOf("No native image generation")
    ),
    AiTool(
        "Midjourney", ToolCategory.IMAGE,
        "Text-to-image generation known for painterly, artistic output.",
        "midjourney.com", "Paid only", 4.6f,
        listOf("High visual quality"), listOf("Discord-based workflow")
    ),
    AiTool(
        "GitHub Copilot", ToolCategory.CODING,
        "In-editor AI pair programmer with inline code suggestions.",
        "github.com/features/copilot", "Free for students", 4.5f,
        listOf("Deep IDE integration"), listOf("Can suggest outdated patterns")
    )
)
