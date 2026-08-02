package com.aiforall.app.domain.repository

import com.aiforall.app.domain.model.ModerationStatus

/**
 * Admin-only actions on a user's account-wide moderation status. Applying
 * a status also re-stamps that user's existing posts (see
 * ModerationRepositoryImpl) so feed filtering reflects the change
 * immediately, without needing a live per-post author lookup.
 */
interface ModerationRepository {
    /** Direct set — admin picks the exact level (e.g. straight to BANNED for a severe violation). */
    suspend fun setModerationStatus(uid: String, status: ModerationStatus): Result<Unit>

    /** Bumps one step up the ladder from the user's current status (ACTIVE -> WARNED -> RESTRICTED -> SHADOWBANNED -> BANNED) and increments their strike count. */
    suspend fun escalate(uid: String): Result<ModerationStatus>
}
