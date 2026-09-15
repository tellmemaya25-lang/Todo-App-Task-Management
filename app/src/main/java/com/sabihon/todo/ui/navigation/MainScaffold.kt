package com.sabihon.todo.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings

sealed class BottomTab(val route: String, val label: String, val icon: @Composable () -> Unit) {
    data object Home : BottomTab("home", "Home", { Icon(Icons.Filled.Home, contentDescription = "Home") })
    data object Search : BottomTab("search", "Search", { Icon(Icons.Filled.Search, contentDescription = "Search") })
    data object History : BottomTab("history", "History", { Icon(Icons.Filled.History, contentDescription = "History") })
    data object Profile : BottomTab("profile", "Profile", { Icon(Icons.Filled.Person, contentDescription = "Profile") })
    data object Settings : BottomTab("settings", "Settings", { Icon(Icons.Filled.Settings, contentDescription = "Settings") })
}

@Composable
fun MainScaffold(
    navController: NavController,
    content: @Composable (Modifier) -> Unit
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: ""

    // Show bottom bar only on main tabs – Home | Search | + | History | Profile
    val showBottomBar = currentRoute.contains("Home") || 
                        currentRoute.contains("Search") || 
                        currentRoute.contains("History") || 
                        currentRoute.contains("Profile") || 
                        currentRoute.contains("Settings") ||
                        currentRoute.contains("Categories") ||
                        currentRoute.contains("AllTasks")

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    val tabs = listOf(
                        BottomTab.Home,
                        BottomTab.Search,
                        BottomTab.History,
                        BottomTab.Profile
                    )

                    tabs.forEach { tab ->
                        val isSelected = when (tab) {
                            is BottomTab.Home -> currentRoute.contains("Home") || currentRoute.contains("AllTasks") || currentRoute.contains("Categories")
                            is BottomTab.Search -> currentRoute.contains("Search")
                            is BottomTab.History -> currentRoute.contains("History")
                            is BottomTab.Profile -> currentRoute.contains("Profile") || currentRoute.contains("Settings")
                            else -> false
                        }

                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                when (tab) {
                                    is BottomTab.Home -> navController.navigate(Route.Home) {
                                        popUpTo(Route.Home) { inclusive = false }
                                        launchSingleTop = true
                                    }
                                    is BottomTab.Search -> navController.navigate(Route.Search) {
                                        popUpTo(Route.Home) { inclusive = false }
                                        launchSingleTop = true
                                    }
                                    is BottomTab.History -> navController.navigate(Route.History) {
                                        popUpTo(Route.Home) { inclusive = false }
                                        launchSingleTop = true
                                    }
                                    is BottomTab.Profile -> navController.navigate(Route.Profile) {
                                        popUpTo(Route.Home) { inclusive = false }
                                        launchSingleTop = true
                                    }
                                    else -> {}
                                }
                            },
                            icon = { tab.icon() },
                            label = { Text(tab.label, style = MaterialTheme.typography.labelSmall) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            if (showBottomBar) {
                // + in middle as FAB – matches Home | Search | + | History | Profile
                FloatingActionButton(
                    onClick = { navController.navigate(Route.AddEditTask()) },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Add Task", modifier = Modifier.size(28.dp))
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            content(Modifier)
        }
    }
}
