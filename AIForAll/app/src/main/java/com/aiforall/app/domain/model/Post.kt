package com.aiforall.app.domain.model

/**
 * A single user-generated post: AI/tech news, a tool, a theory/
 * discussion, or (club members only) an Event. Posts publish
 * immediately — there is no pre-approval queue. What keeps the feed
 * usable is reactive moderation: any post can be reported, and enough
 * reports (or a direct admin decision) escalates the AUTHOR's
 * moderation status, which in turn hides their future/existing posts.
 * See ModerationStatus below and ModerationRepository.
 *
 * `authorModerationStatus` is a deliberate denormalization: it's a
 * snapshot of the author's status AT THE TIME a moderation action last
 * touched this post, copied here so the feed query can filter without
 * a live per-post author lookup. ModerationRepositoryImpl re-stamps a
 * user's existing posts whenever their status changes.
 */
data class Post(
    val id: String = "",
    val authorId: String = "",
    val authorName: String = "",
    val authorPhotoUrl: String? = null,
    val authorModerationStatus: String = ModerationStatus.ACTIVE.name,
    val type: PostType = PostType.DISCUSSION,
    val title: String = "",
    val body: String = "",
    val mediaUrls: List<String> = emptyList(),
    val mediaType: PostMediaType = PostMediaType.NONE,
    val linkUrl: String? = null,       // e.g. source article or tool website
    val createdAt: Long = 0L,
    val likeCount: Int = 0,
    val commentCount: Int = 0,
    val reportCount: Int = 0,
    val visibility: PostVisibility = PostVisibility.VISIBLE
)

enum class PostType { NEWS, TOOL, THEORY, DISCUSSION, EVENT }

enum class PostMediaType { NONE, IMAGE, VIDEO }

/** Per-post admin action (separate from the author-level ModerationStatus ladder) — e.g. removing one bad post without punishing the whole account. */
enum class PostVisibility { VISIBLE, HIDDEN }
