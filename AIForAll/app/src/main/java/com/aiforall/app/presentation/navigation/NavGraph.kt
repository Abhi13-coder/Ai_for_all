package com.aiforall.app.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.aiforall.app.presentation.components.GradientBackground
import com.aiforall.app.presentation.screens.club.ClubScreen
import com.aiforall.app.presentation.screens.community.CommunityFeedScreen
import com.aiforall.app.presentation.screens.community.CreatePostScreen
import com.aiforall.app.presentation.screens.explore.ExploreScreen
import com.aiforall.app.presentation.screens.home.HomeScreen
import com.aiforall.app.presentation.screens.learn.LearnScreen
import com.aiforall.app.presentation.screens.profile.ProfileScreen

/**
 * Single NavHost for the whole app. Bottom-nav destinations and nested
 * (non-bottom-nav) destinations like the community feed / create-post
 * flow all share one back stack, which is what lets "Home -> Create Post"
 * and "Club -> Community Feed -> Create Post" both work naturally with
 * the system back button.
 */
@Composable
fun AiForAllNavGraph() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { AiForAllBottomBar(navController) }
    ) { innerPadding ->
        GradientBackground {
            NavHost(
                navController = navController,
                startDestination = BottomNavItem.Home.route,
                modifier = androidx.compose.ui.Modifier.padding(innerPadding)
            ) {
                composable(BottomNavItem.Home.route) {
                    HomeScreen(onOpenCreatePost = { navController.navigate(Routes.CREATE_POST) })
                }
                composable(BottomNavItem.Explore.route) { ExploreScreen() }
                composable(BottomNavItem.Learn.route) { LearnScreen() }
                composable(BottomNavItem.Club.route) {
                    ClubScreen(onOpenCommunity = { navController.navigate(Routes.COMMUNITY_FEED) })
                }
                composable(BottomNavItem.Profile.route) { ProfileScreen() }

                // Community: user-generated posts about AI news / tools / theories
                composable(Routes.COMMUNITY_FEED) {
                    CommunityFeedScreen(onCreatePost = { navController.navigate(Routes.CREATE_POST) })
                }
                composable(Routes.CREATE_POST) {
                    CreatePostScreen(onPosted = { navController.popBackStack() })
                }
            }
        }
    }
}

@Composable
private fun AiForAllBottomBar(navController: NavHostController) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    NavigationBar {
        BottomNavItem.items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) }
            )
        }
    }
}
