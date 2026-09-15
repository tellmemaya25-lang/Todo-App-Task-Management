package com.sabihon.todo.core.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

sealed class BottomNavItem(val route: String, val label: String, val icon: @Composable () -> Unit) {
    data object Home : BottomNavItem("home", "Home", { Icon(Icons.Filled.Home, contentDescription = "Home") })
    data object Search : BottomNavItem("search", "Search", { Icon(Icons.Filled.Search, contentDescription = "Search") })
    data object Add : BottomNavItem("add", "Add", { 
        Icon(Icons.Filled.Add, contentDescription = "Add", modifier = Modifier.size(24.dp)) 
    })
    data object History : BottomNavItem("history", "History", { Icon(Icons.Filled.History, contentDescription = "History") })
    data object Profile : BottomNavItem("profile", "Profile", { Icon(Icons.Filled.Person, contentDescription = "Profile") })
    data object Settings : BottomNavItem("settings", "Settings", { Icon(Icons.Filled.Settings, contentDescription = "Settings") })
}

@Composable
fun BottomNavBar(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        val items = listOf(
            BottomNavItem.Home,
            BottomNavItem.Search,
            BottomNavItem.Add,
            BottomNavItem.History,
            BottomNavItem.Profile
        )
        
        items.forEach { item ->
            val isSelected = when (item) {
                is BottomNavItem.Home -> currentRoute == "home" || currentRoute.startsWith("home")
                is BottomNavItem.Search -> currentRoute == "search"
                is BottomNavItem.Add -> false // Add is FAB-like, never selected
                is BottomNavItem.History -> currentRoute == "history"
                is BottomNavItem.Profile -> currentRoute == "profile" || currentRoute == "settings"
                else -> false
            }
            
            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    if (item is BottomNavItem.Add) {
                        onNavigate("add_edit")
                    } else {
                        onNavigate(item.route)
                    }
                },
                icon = { item.icon() },
                label = { Text(item.label, style = MaterialTheme.typography.labelSmall) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    }
}

@Composable
fun BottomNavBarWithSettings(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        val items = listOf(
            BottomNavItem.Home,
            BottomNavItem.Search,
            BottomNavItem.Add,
            BottomNavItem.History,
            BottomNavItem.Settings
        )
        
        items.forEach { item ->
            val isSelected = when {
                item.route == "home" -> currentRoute == "home" || currentRoute == "all_tasks"
                item.route == "search" -> currentRoute == "search"
                item.route == "history" -> currentRoute == "history"
                item.route == "settings" -> currentRoute == "settings" || currentRoute == "profile"
                else -> false
            }
            
            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    if (item is BottomNavItem.Add) {
                        onNavigate("add_edit")
                    } else {
                        onNavigate(item.route)
                    }
                },
                icon = { item.icon() },
                label = { Text(item.label, style = MaterialTheme.typography.labelSmall) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    }
}
