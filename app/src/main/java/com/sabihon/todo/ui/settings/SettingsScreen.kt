package com.sabihon.todo.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SettingsBrightness
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sabihon.todo.core.datastore.ThemePref
import com.sabihon.todo.ui.profile.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit = {},
    onNavigateToHistory: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToAbout: () -> Unit = {},
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Theme Section
            item {
                Text("Appearance", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(vertical = 8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Theme", style = MaterialTheme.typography.titleSmall)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(
                                selected = uiState.theme == ThemePref.SYSTEM,
                                onClick = { viewModel.setTheme(ThemePref.SYSTEM) },
                                label = { Text("System") },
                                leadingIcon = { Icon(Icons.Filled.SettingsBrightness, contentDescription = null, modifier = Modifier.size(18.dp)) }
                            )
                            FilterChip(
                                selected = uiState.theme == ThemePref.LIGHT,
                                onClick = { viewModel.setTheme(ThemePref.LIGHT) },
                                label = { Text("Light") },
                                leadingIcon = { Icon(Icons.Filled.LightMode, contentDescription = null, modifier = Modifier.size(18.dp)) }
                            )
                            FilterChip(
                                selected = uiState.theme == ThemePref.DARK,
                                onClick = { viewModel.setTheme(ThemePref.DARK) },
                                label = { Text("Dark") },
                                leadingIcon = { Icon(Icons.Filled.DarkMode, contentDescription = null, modifier = Modifier.size(18.dp)) }
                            )
                        }
                    }
                }
            }

            // History Section - Migrated from notification icon
            item {
                Text("Activity", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(vertical = 8.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToHistory() },
                    shape = RoundedCornerShape(16.dp)
                ) {
                    ListItem(
                        headlineContent = { Text("History") },
                        supportingContent = { Text("View completed tasks and activity log") },
                        leadingContent = {
                            Icon(Icons.Filled.History, contentDescription = null)
                        }
                    )
                }
            }

            // Profile Section
            item {
                Text("Account", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(vertical = 8.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToProfile() },
                    shape = RoundedCornerShape(16.dp)
                ) {
                    ListItem(
                        headlineContent = { Text("Profile") },
                        supportingContent = { Text("Edit profile and picture") },
                        leadingContent = {
                            Icon(Icons.Filled.Person, contentDescription = null)
                        }
                    )
                }
            }

            // About Section
            item {
                Text("About", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(vertical = 8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Just Todo-it v1.0", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "A clean, pastel, production-quality To-Do app built with Kotlin + Jetpack Compose + Firebase. Features offline-first Firestore, reminders, categories, search, and more.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(8.dp))
                        Text("Tech Stack", style = MaterialTheme.typography.titleSmall)
                        Text(
                            "• Kotlin 2.x + Compose BOM + Material 3\n" +
                            "• MVVM + Hilt + Coroutines Flow\n" +
                            "• Firebase Auth (Email + Google OAuth)\n" +
                            "• Firestore offline + WorkManager reminders\n" +
                            "• DataStore for theme preferences",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(8.dp))
                        Text("Features", style = MaterialTheme.typography.titleSmall)
                        Text(
                            "• 4 filter chips: Today, Completed, Pending, All\n" +
                            "• Task submenu: Edit, Delete, Mark Done\n" +
                            "• Recent search suggestions\n" +
                            "• Profile picture editing\n" +
                            "• Light/Dark/System theme",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                Spacer(Modifier.height(8.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToAbout() },
                    shape = RoundedCornerShape(16.dp)
                ) {
                    ListItem(
                        headlineContent = { Text("More Info") },
                        supportingContent = { Text("Version, licenses, privacy") },
                        leadingContent = {
                            Icon(Icons.Filled.Info, contentDescription = null)
                        }
                    )
                }
            }

            item {
                Spacer(Modifier.height(80.dp))
            }
        }
    }
}
