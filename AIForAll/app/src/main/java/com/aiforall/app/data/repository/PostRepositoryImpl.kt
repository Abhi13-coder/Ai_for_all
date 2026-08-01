package com.aiforall.app.data.repository

import com.aiforall.app.domain.model.Post
import com.aiforall.app.domain.model.PostStatus
import com.aiforall.app.domain.repository.PostRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

/**
 * Firestore-backed implementation. Collection layout:
 *   posts/{postId}  -> Post fields, status field gates visibility
 * Images go to Storage under posts/{postId}/image.jpg and the resulting
 * download URL is written back onto the Post document.
 *
 * New posts are written with status = PENDING_REVIEW; only the query in
 * observeApprovedPosts() (status == APPROVED) is shown in the public
 * feed, so moderation is enforced by the query itself, not just client UI.
 */
class PostRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) : PostRepository {

    private val postsRef = firestore.collection("posts")

    override fun observeApprovedPosts(): Flow<List<Post>> = callbackFlow {
        val registration = postsRef
            .whereEqualTo("status", PostStatus.APPROVED.name)
            .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
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

    override suspend fun createPost(post: Post, imageBytes: ByteArray?): Result<Unit> = runCatching {
        val id = post.id.ifBlank { UUID.randomUUID().toString() }
        var imageUrl = post.imageUrl

        if (imageBytes != null) {
            val ref = storage.reference.child("posts/$id/image.jpg")
            ref.putBytes(imageBytes).await()
            imageUrl = ref.downloadUrl.await().toString()
        }

        val toWrite = post.copy(
            id = id,
            imageUrl = imageUrl,
            createdAt = System.currentTimeMillis(),
            status = PostStatus.PENDING_REVIEW
        )
        postsRef.document(id).set(toWrite).await()
    }

    override suspend fun likePost(postId: String, userId: String): Result<Unit> = runCatching {
        // Stored as a sub-collection (posts/{id}/likes/{userId}) so a user
        // can only like once, and likeCount is bumped via a transaction.
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
        postsRef.document(postId).collection("reports").document(userId).set(
            mapOf("reason" to reason, "reportedAt" to System.currentTimeMillis())
        ).await()
    }
}
