package com.aiforall.app.domain.repository

import com.aiforall.app.domain.model.Post
import kotlinx.coroutines.flow.Flow

/**
 * Contract for the community-post feature. Kept in `domain` (no Firebase
 * imports here) so the ViewModel layer never depends on a specific
 * backend — swapping Firestore for something else later only touches
 * the `data` layer implementation.
 */
interface PostRepository {
    fun observeApprovedPosts(): Flow<List<Post>>
    suspend fun createPost(post: Post, imageBytes: ByteArray?): Result<Unit>
    suspend fun likePost(postId: String, userId: String): Result<Unit>
    suspend fun reportPost(postId: String, userId: String, reason: String): Result<Unit>
}
