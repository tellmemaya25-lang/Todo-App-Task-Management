package com.sabihon.todo.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sabihon.todo.core.ui.theme.AccentBlue

/**
 * Bottom nav matching reference image – floating white card with 24.dp rounded corners,
 * 5 items: Home, Search, + (center FAB), History, Profile
 * Selected item blue, others grey, with subtle shadow.
 */
data class BottomNavTab(
    val label: String,
    val icon: ImageVector,
    val route: String
)

@Composable
fun RoundedBottomNavBar(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val tabs = listOf(
        BottomNavTab("Home", Icons.Filled.Home, "home"),
        BottomNavTab("Search", Icons.Filled.Search, "search"),
        BottomNavTab("Add", Icons.Filled.Add, "add"),
        BottomNavTab("History", Icons.Filled.History, "history"),
        BottomNavTab("Profile", Icons.Filled.Person, "profile")
    )

    // Floating container with 24.dp radius like image
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .shadow(8.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 4.dp
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                tabs.forEach { tab ->
                    val isSelected = when (tab.route) {
                        "home" -> currentRoute.contains("Home") || currentRoute.contains("AllTasks") || currentRoute.contains("Categories")
                        "search" -> currentRoute.contains("Search")
                        "history" -> currentRoute.contains("History")
                        "profile" -> currentRoute.contains("Profile") || currentRoute.contains("Settings")
                        "add" -> false
                        else -> false
                    }

                    // Middle + button special styling
                    if (tab.route == "add") {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(AccentBlue)
                                .clickable { onNavigate("add") },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = "Add Task",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    } else {
                        Column(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { onNavigate(tab.route) }
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.label,
                                tint = if (isSelected) AccentBlue else Color.Gray,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = tab.label,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                ),
                                color = if (isSelected) AccentBlue else Color.Gray
                            )
                        }
                    }
                }
            }
            // iOS-like home indicator handle like in image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(100.dp))
                        .background(Color.LightGray.copy(alpha = 0.5f))
                )
            }
        }
    }
}

// Legacy simple version for fallback
@Composable
fun BottomNavBarSimple(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    RoundedBottomNavBar(currentRoute = currentRoute, onNavigate = onNavigate, modifier = modifier)
}
