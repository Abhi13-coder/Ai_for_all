package com.aiforall.app.domain.repository

import android.net.Uri
import com.aiforall.app.domain.model.Post
import com.aiforall.app.domain.model.PostMediaType
import kotlinx.coroutines.flow.Flow

/**
 * Contract for posts. Kept in `domain` for the ViewModel-facing shape,
 * though `Uri` here is a pragmatic exception to "no platform types in
 * domain" — streaming media straight from a content Uri to Storage
 * (rather than loading it fully into a ByteArray first) is what keeps
 * video uploads from OOM-ing on a phone, and threading a byte array
 * through an extra abstraction layer wasn't worth it for this app's size.
 */
interface PostRepository {
    /** Full feed, already filtered to exclude BANNED authors' posts, admin-hidden posts, and (for anyone but the author) SHADOWBANNED authors' posts. */
    fun observeFeedPosts(currentUserId: String): Flow<List<Post>>

    /** Posts with reportCount > 0, for the admin moderation screen. */
    fun observeReportedPosts(): Flow<List<Post>>

    suspend fun createPost(
        post: Post,
        mediaUris: List<Uri> = emptyList(),
        mediaType: PostMediaType = PostMediaType.NONE
    ): Result<Unit>

    suspend fun likePost(postId: String, userId: String): Result<Unit>
    suspend fun reportPost(postId: String, userId: String, reason: String): Result<Unit>

    // --- admin actions on individual posts (in-app, no console needed) ---
    suspend fun hidePost(postId: String): Result<Unit>
    suspend fun dismissReports(postId: String): Result<Unit>
}
