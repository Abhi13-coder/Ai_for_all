package com.aiforall.app.domain.model

/**
 * One doc per signed-up user (users/{uid} in Firestore). Two axes here,
 * kept deliberately separate:
 *
 * - `membershipTier`: what AI BuildFest gave them (recognition/perks —
 *   read-mostly, changed by an admin marking results)
 * - `clubRole`: whether they can actually DO things in the app (post
 *   official events, moderate community posts) — granted by redeeming
 *   a club code, not by winning a competition. A BuildFest winner and
 *   a random public installer both start with clubRole = NONE; only
 *   redeeming a real club code grants MEMBER/ADMIN.
 */
data class UserProfile(
    val uid: String = "",
    val displayName: String = "",
    val email: String = "",
    val photoUrl: String? = null,
    val membershipTier: MembershipTier = MembershipTier.PUBLIC,
    val clubRole: ClubRole = ClubRole.NONE,
    val badges: List<String> = emptyList(),
    val createdAt: Long = 0L,
    val moderationStatus: ModerationStatus = ModerationStatus.ACTIVE,
    val strikeCount: Int = 0
)

/**
 * Escalation ladder for content-policy violations (posts must stay
 * AI/tech-related; no sexual content). There's no automatic topic
 * classifier — enforcement is reactive: users report posts, an ADMIN
 * reviews reports in-app and applies one of these. Each level is
 * strictly more restrictive than the last:
 *
 * - ACTIVE: normal
 * - WARNED: can still post; a warning banner shows in their own app
 * - RESTRICTED: cannot create new posts (existing posts stay visible)
 * - SHADOWBANNED: can still post, but posts are invisible to everyone
 *   except the author themselves (they don't know they're hidden)
 * - BANNED: fully blocked — cannot post, and all their posts are
 *   hidden from everyone
 */
enum class ModerationStatus { ACTIVE, WARNED, RESTRICTED, SHADOWBANNED, BANNED }

/** What AI BuildFest results earned them. Set by an admin, not self-serve. */
enum class MembershipTier {
    PUBLIC,             // anyone who just installed the app
    PARTICIPANT,        // took part in AI BuildFest — temporary premium
    SECOND_PLACE,       // extended premium + badge
    THIRD_PLACE,        // extended premium + badge
    WINNER_LIFETIME      // lifetime premium, Hall of Fame, Champion badge
}

/**
 * What they're allowed to DO in the app. Unlocked by redeeming a club
 * code (see ClubCode below) — completely independent of competition
 * results.
 */
enum class ClubRole {
    NONE,     // ordinary user — can browse, and post community discussion/news/tool/theory
    MEMBER,   // verified AI Club member — same as above, plus can post official Events
    ADMIN     // club leadership — MEMBER privileges + can moderate reported posts/users in-app
}

/**
 * A redeemable code (club_codes/{code} in Firestore) that grants a
 * ClubRole when entered. Kept server-side only — the app never
 * generates or displays these, admins create them directly in the
 * Firebase console (or a future admin screen).
 */
data class ClubCode(
    val code: String = "",
    val grantsRole: ClubRole = ClubRole.MEMBER,
    val maxUses: Int = 1,
    val usesRemaining: Int = 1,
    val active: Boolean = true
)
