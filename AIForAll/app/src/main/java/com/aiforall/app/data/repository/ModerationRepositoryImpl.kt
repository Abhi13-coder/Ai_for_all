package com.aiforall.app.data.repository

import com.aiforall.app.domain.model.ModerationStatus
import com.aiforall.app.domain.repository.ModerationRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * Applying a status does two writes: the user's own doc (source of
 * truth for future posts and for gating new-post creation), and a
 * batch update across their EXISTING posts' denormalized
 * `authorModerationStatus` field, so the feed reflects the change on
 * already-published posts too, not just ones made after the action.
 */
class ModerationRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : ModerationRepository {

    private val usersRef = firestore.collection("users")
    private val postsRef = firestore.collection("posts")

    override suspend fun setModerationStatus(uid: String, status: ModerationStatus): Result<Unit> = runCatching {
        usersRef.document(uid).update("moderationStatus", status.name).await()
        restampAuthorPosts(uid, status)
    }

    override suspend fun escalate(uid: String): Result<ModerationStatus> = runCatching {
        val ladder = listOf(
            ModerationStatus.ACTIVE,
            ModerationStatus.WARNED,
            ModerationStatus.RESTRICTED,
            ModerationStatus.SHADOWBANNED,
            ModerationStatus.BANNED
        )
        val snapshot = usersRef.document(uid).get().await()
        val currentName = snapshot.getString("moderationStatus") ?: ModerationStatus.ACTIVE.name
        val currentIndex = ladder.indexOfFirst { it.name == currentName }.coerceAtLeast(0)
        val next = ladder.getOrElse(currentIndex + 1) { ladder.last() }

        val currentStrikes = (snapshot.getLong("strikeCount") ?: 0L).toInt()
        usersRef.document(uid).update(
            mapOf(
                "moderationStatus" to next.name,
                "strikeCount" to currentStrikes + 1
            )
        ).await()
        restampAuthorPosts(uid, next)
        next
    }

    /** Firestore batches cap at 500 writes — chunk defensively even though one user's post count should be small in practice. */
    private suspend fun restampAuthorPosts(uid: String, status: ModerationStatus) {
        val authorPosts = postsRef.whereEqualTo("authorId", uid).get().await()
        authorPosts.documents.chunked(400).forEach { chunk ->
            val batch = firestore.batch()
            chunk.forEach { doc -> batch.update(doc.reference, "authorModerationStatus", status.name) }
            batch.commit().await()
        }
    }
}
