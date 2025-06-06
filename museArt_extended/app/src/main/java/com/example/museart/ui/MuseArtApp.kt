package com.example.museart.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.museart.R
import com.example.museart.ui.screens.auth.LoginScreen
import com.example.museart.ui.screens.compose.ComposeScreen
import com.example.museart.ui.screens.feed.FeedScreen
import com.example.museart.ui.screens.notifications.NotificationsScreen
import com.example.museart.ui.screens.profile.ProfileScreen
import com.example.museart.ui.screens.search.SearchScreen
import com.example.museart.ui.theme.Blue

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Feed : Screen("feed")
    object Search : Screen("search")
    object Compose : Screen("compose")
    object Notifications : Screen("notifications")
    object Profile : Screen("profile")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MuseArtApp() {
    val navController = rememberNavController()
    val items = listOf(
        Screen.Feed to Icons.Default.Home,
        Screen.Search to Icons.Default.Search,
        Screen.Notifications to Icons.Default.Notifications,
        Screen.Profile to Icons.Default.Person
    )
    
    // Для демонстрации начинаем с экрана ленты, в реальном приложении нужно проверять авторизацию
    val startDestination = Screen.Feed.route
    
    Scaffold(
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            
            // Не показываем нижнюю навигацию на экране входа и создания поста
            if (currentDestination?.route != Screen.Login.route && 
                currentDestination?.route != Screen.Compose.route) {
                BottomNavigation(
                    backgroundColor = MaterialTheme.colorScheme.surface,
                    contentColor = Blue
                ) {
                    items.forEach { (screen, icon) ->
                        BottomNavigationItem(
                            icon = { Icon(icon, contentDescription = null) },
                            label = { 
                                Text(
                                    text = when (screen) {
                                        Screen.Feed -> stringResource(R.string.home)
                                        Screen.Search -> stringResource(R.string.search)
                                        Screen.Notifications -> stringResource(R.string.notifications)
                                        Screen.Profile -> stringResource(R.string.profile)
                                        else -> ""
                                    }
                                ) 
                            },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            
            // Показываем FAB только на основных экранах, не на экране входа и создания поста
            if (currentDestination?.route != Screen.Login.route && 
                currentDestination?.route != Screen.Compose.route) {
                FloatingActionButton(
                    onClick = { navController.navigate(Screen.Compose.route) },
                    containerColor = Blue
                ) {
                    Text("+", fontSize = 24.sp, color = MaterialTheme.colorScheme.onPrimary)
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Login.route) {
                LoginScreen(
                    onLoginSuccess = { navController.navigate(Screen.Feed.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }}
                )
            }
            composable(Screen.Feed.route) {
                FeedScreen()
            }
            composable(Screen.Search.route) {
                SearchScreen()
            }
            composable(Screen.Compose.route) {
                ComposeScreen(
                    onPostCreated = { navController.popBackStack() }
                )
            }
            composable(Screen.Notifications.route) {
                NotificationsScreen()
            }
            composable(Screen.Profile.route) {
                ProfileScreen()
            }
        }
    }
}

