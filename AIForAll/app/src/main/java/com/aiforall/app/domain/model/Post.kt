package com.aiforall.app.domain.model

/**
 * A single user-generated community post: news they found, a tool
 * they're sharing, or a theory/discussion topic. `type` drives which
 * chip/icon shows in the feed and lets Explore/Learn later pull in
 * "community-sourced" tools or news alongside curated ones.
 */
data class Post(
    val id: String = "",
    val authorId: String = "",
    val authorName: String = "",
    val authorPhotoUrl: String? = null,
    val type: PostType = PostType.DISCUSSION,
    val title: String = "",
    val body: String = "",
    val imageUrl: String? = null,
    val linkUrl: String? = null,       // e.g. source article or tool website
    val createdAt: Long = 0L,
    val likeCount: Int = 0,
    val commentCount: Int = 0,
    val status: PostStatus = PostStatus.PENDING_REVIEW
)

enum class PostType { NEWS, TOOL, THEORY, DISCUSSION }

/**
 * Lightweight moderation gate so an open posting feature doesn't
 * immediately fill the club feed with spam — posts land as
 * PENDING_REVIEW and a club admin/mod flips them to APPROVED (or
 * REJECTED) before they're visible to everyone else.
 */
enum class PostStatus { PENDING_REVIEW, APPROVED, REJECTED }
