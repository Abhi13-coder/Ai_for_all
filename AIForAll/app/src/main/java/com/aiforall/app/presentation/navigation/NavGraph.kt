package com.aiforall.app.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.aiforall.app.presentation.components.GradientBackground
import com.aiforall.app.presentation.screens.auth.AuthViewModel
import com.aiforall.app.presentation.screens.auth.SignInScreen
import com.aiforall.app.presentation.screens.auth.SignUpScreen
import com.aiforall.app.presentation.screens.club.ClubScreen
import com.aiforall.app.presentation.screens.community.CommunityFeedScreen
import com.aiforall.app.presentation.screens.community.CreatePostScreen
import com.aiforall.app.presentation.screens.explore.ExploreScreen
import com.aiforall.app.presentation.screens.home.HomeScreen
import com.aiforall.app.presentation.screens.learn.LearnScreen
import com.aiforall.app.presentation.screens.moderation.AdminModerationScreen
import com.aiforall.app.presentation.screens.profile.ProfileScreen

/**
 * Top-level gate: signed-out users only ever see the auth flow
 * (SignIn/SignUp); signed-in users get the full app. Switching between
 * the two intentionally uses separate NavHosts — going from
 * signed-out to signed-in is a hard state change, not something that
 * should preserve back-stack history either direction.
 */
@Composable
fun AiForAllNavGraph() {
    val authViewModel: AuthViewModel = hiltViewModel()
    val currentUser by authViewModel.currentUser.collectAsState()

    if (currentUser == null) {
        AuthNavHost()
    } else {
        MainNavHost()
    }
}

@Composable
private fun AuthNavHost() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Routes.SIGN_IN) {
        composable(Routes.SIGN_IN) {
            SignInScreen(
                onSignedIn = { /* handled by the gate above once currentUser flips */ },
                onGoToSignUp = { navController.navigate(Routes.SIGN_UP) }
            )
        }
        composable(Routes.SIGN_UP) {
            SignUpScreen(
                onSignedUp = { /* handled by the gate above once currentUser flips */ },
                onGoToSignIn = { navController.popBackStack() }
            )
        }
    }
}

@Composable
private fun MainNavHost() {
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
                    ClubScreen(
                        onOpenCommunity = { navController.navigate(Routes.COMMUNITY_FEED) },
                        onOpenModeration = { navController.navigate(Routes.ADMIN_MODERATION) }
                    )
                }
                composable(BottomNavItem.Profile.route) { ProfileScreen() }

                // Community: user-generated posts about AI news / tools / theories / events
                composable(Routes.COMMUNITY_FEED) {
                    CommunityFeedScreen(onCreatePost = { navController.navigate(Routes.CREATE_POST) })
                }
                composable(Routes.CREATE_POST) {
                    CreatePostScreen(onPosted = { navController.popBackStack() })
                }
                composable(Routes.ADMIN_MODERATION) {
                    AdminModerationScreen()
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
