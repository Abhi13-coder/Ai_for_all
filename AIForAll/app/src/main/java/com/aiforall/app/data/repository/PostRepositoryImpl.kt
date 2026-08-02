package com.aiforall.app.data.repository

import android.net.Uri
import com.aiforall.app.domain.model.ModerationStatus
import com.aiforall.app.domain.model.Post
import com.aiforall.app.domain.model.PostMediaType
import com.aiforall.app.domain.model.PostVisibility
import com.aiforall.app.domain.repository.PostRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

/**
 * Firestore-backed. Posts publish immediately (no PENDING_REVIEW) —
 * moderation is reactive: reports accumulate on reportCount, and an
 * admin either hides an individual post (visibility = HIDDEN) or
 * escalates the whole author via ModerationRepository, which re-stamps
 * `authorModerationStatus` onto every one of that author's posts so
 * observeFeedPosts() can filter without a live per-post author lookup.
 */
class PostRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) : PostRepository {

    private val postsRef = firestore.collection("posts")

    override fun observeFeedPosts(currentUserId: String): Flow<List<Post>> =
        rawPostsQuery().map { posts ->
            posts.filter { post ->
                post.visibility == PostVisibility.VISIBLE &&
                    post.authorModerationStatus != ModerationStatus.BANNED.name &&
                    (post.authorModerationStatus != ModerationStatus.SHADOWBANNED.name || post.authorId == currentUserId)
            }
        }

    override fun observeReportedPosts(): Flow<List<Post>> =
        rawPostsQuery().map { posts -> posts.filter { it.reportCount > 0 } }

    private fun rawPostsQuery(): Flow<List<Post>> = callbackFlow {
        val registration = postsRef
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val posts = snapshot?.documents?.mapNotNull { it.toObject(Post::class.java) } ?: emptyList()
                trySend(posts)
            }
        awaitClose { registration.remove() }
    }

    override suspend fun createPost(
        post: Post,
        mediaUris: List<Uri>,
        mediaType: PostMediaType
    ): Result<Unit> = runCatching {
        val id = post.id.ifBlank { UUID.randomUUID().toString() }

        val mediaUrls = mediaUris.mapIndexed { index, uri ->
            val ref = storage.reference.child("posts/$id/media_$index")
            ref.putFile(uri).await()
            ref.downloadUrl.await().toString()
        }

        val toWrite = post.copy(
            id = id,
            mediaUrls = mediaUrls,
            mediaType = mediaType,
            createdAt = System.currentTimeMillis(),
            visibility = PostVisibility.VISIBLE
        )
        postsRef.document(id).set(toWrite).await()
    }

    override suspend fun likePost(postId: String, userId: String): Result<Unit> = runCatching {
        firestore.runTransaction { txn ->
            val likeRef = postsRef.document(postId).collection("likes").document(userId)
            if (txn.get(likeRef).exists()) return@runTransaction null
            txn.set(likeRef, mapOf("likedAt" to System.currentTimeMillis()))
            val postRef = postsRef.document(postId)
            val current = txn.get(postRef).getLong("likeCount") ?: 0
            txn.update(postRef, "likeCount", current + 1)
            null
        }.await()
    }

    override suspend fun reportPost(postId: String, userId: String, reason: String): Result<Unit> = runCatching {
        val postRef = postsRef.document(postId)
        val reportRef = postRef.collection("reports").document(userId)

        firestore.runTransaction { txn ->
            if (txn.get(reportRef).exists()) return@runTransaction null // one report per user per post
            txn.set(reportRef, mapOf("reason" to reason, "reportedAt" to System.currentTimeMillis()))
            val current = txn.get(postRef).getLong("reportCount") ?: 0
            txn.update(postRef, "reportCount", current + 1)
            null
        }.await()
    }

    override suspend fun hidePost(postId: String): Result<Unit> = runCatching {
        postsRef.document(postId).update("visibility", PostVisibility.HIDDEN.name).await()
    }

    override suspend fun dismissReports(postId: String): Result<Unit> = runCatching {
        postsRef.document(postId).update("reportCount", 0).await()
    }
}
