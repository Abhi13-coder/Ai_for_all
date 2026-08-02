package com.aiforall.app.data.repository

import com.aiforall.app.domain.model.ClubRole
import com.aiforall.app.domain.model.UserProfile
import com.aiforall.app.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * Firebase Auth (identity) + Firestore users/{uid} (profile/role) doc.
 * `currentUser` reacts to both sign-in/out AND live edits to the
 * profile doc (e.g. a club code redemption updating clubRole shows up
 * immediately without a re-login).
 */
class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {

    private val usersRef = firestore.collection("users")
    private val clubCodesRef = firestore.collection("club_codes")

    @OptIn(ExperimentalCoroutinesApi::class)
    override val currentUser: Flow<UserProfile?> = authStateUid().flatMapLatest { uid ->
        if (uid == null) flowOf(null) else observeUserDoc(uid)
    }

    private fun authStateUid(): Flow<String?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser?.uid)
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    private fun observeUserDoc(uid: String): Flow<UserProfile?> = callbackFlow {
        val registration = usersRef.document(uid).addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            trySend(snapshot?.toObject(UserProfile::class.java))
        }
        awaitClose { registration.remove() }
    }

    override suspend fun signUp(email: String, password: String, displayName: String): Result<Unit> = runCatching {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        val uid = result.user?.uid ?: error("Sign-up succeeded but no user id was returned.")
        val profile = UserProfile(
            uid = uid,
            displayName = displayName,
            email = email,
            createdAt = System.currentTimeMillis()
        )
        usersRef.document(uid).set(profile).await()
    }

    override suspend fun signIn(email: String, password: String): Result<Unit> = runCatching {
        auth.signInWithEmailAndPassword(email, password).await()
        Unit
    }

    override fun signOut() = auth.signOut()

    override suspend fun redeemClubCode(code: String): Result<Unit> = runCatching {
        val uid = auth.currentUser?.uid ?: error("Not signed in.")
        val codeRef = clubCodesRef.document(code)
        val userRef = usersRef.document(uid)

        firestore.runTransaction { txn ->
            val codeSnap = txn.get(codeRef)
            if (!codeSnap.exists()) error("That code isn't valid.")

            val active = codeSnap.getBoolean("active") ?: false
            val usesRemaining = (codeSnap.getLong("usesRemaining") ?: 0L).toInt()
            if (!active || usesRemaining <= 0) error("That code has expired or been fully used.")

            val grantsRoleName = codeSnap.getString("grantsRole") ?: ClubRole.MEMBER.name
            txn.update(codeRef, "usesRemaining", usesRemaining - 1)
            txn.update(userRef, "clubRole", grantsRoleName)
            null
        }.await()
    }
}
