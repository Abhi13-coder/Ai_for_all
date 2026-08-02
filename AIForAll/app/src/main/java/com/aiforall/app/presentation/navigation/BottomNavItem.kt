package com.aiforall.app.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Person
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Bottom nav is 5 destinations per the spec (Home, Explore, Learn, Club,
 * Profile). The new "post news / tools / theories" community feature is
 * NOT given its own bottom-nav slot — 6 tabs would crowd the bar and
 * break the minimal-icon requirement. Instead it lives as a sub-section
 * inside Club (the natural home for AI-Club social content) with its own
 * nested route, plus a floating "+" entry point from Home's quick actions.
 */
sealed class BottomNavItem(val route: String, val label: String, val icon: ImageVector) {
    data object Home     : BottomNavItem("home", "Home", Icons.Outlined.Home)
    data object Explore  : BottomNavItem("explore", "Explore", Icons.Outlined.Explore)
    data object Learn    : BottomNavItem("learn", "Learn", Icons.Outlined.MenuBook)
    data object Club     : BottomNavItem("club", "Club", Icons.Outlined.Groups)
    data object Profile  : BottomNavItem("profile", "Profile", Icons.Outlined.Person)

    companion object {
        val items = listOf(Home, Explore, Learn, Club, Profile)
    }
}

/** Nested routes not on the bottom bar. */
object Routes {
    const val SIGN_IN = "sign_in"
    const val SIGN_UP = "sign_up"
    const val COMMUNITY_FEED = "community_feed"
    const val CREATE_POST = "create_post"
    const val ADMIN_MODERATION = "admin_moderation"
    const val POST_DETAIL = "post_detail/{postId}"
    fun postDetail(postId: String) = "post_detail/$postId"
}
