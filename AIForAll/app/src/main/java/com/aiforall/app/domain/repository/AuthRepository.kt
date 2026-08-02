package com.aiforall.app.domain.repository

import com.aiforall.app.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

/**
 * Contract for auth + the signed-in user's profile doc. `currentUser`
 * emits null when signed out and the live UserProfile when signed in —
 * NavGraph watches this to decide whether to show the auth flow or the
 * main app.
 */
interface AuthRepository {
    val currentUser: Flow<UserProfile?>

    suspend fun signUp(email: String, password: String, displayName: String): Result<Unit>
    suspend fun signIn(email: String, password: String): Result<Unit>
    fun signOut()

    /** Validates the code against club_codes/{code} and, if valid, upgrades the caller's ClubRole. */
    suspend fun redeemClubCode(code: String): Result<Unit>
}
