package com.sabihon.todo.core.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

/**
 * Top bar with iOS-like clean style.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SabihonTopBar(
    title: String,
    modifier: Modifier = Modifier,
    showBack: Boolean = false,
    onBackClick: (() -> Unit)? = null,
    showSearch: Boolean = false,
    onSearchClick: (() -> Unit)? = null,
    showNotifications: Boolean = false,
    onNotificationClick: (() -> Unit)? = null,
    showMore: Boolean = false,
    onMoreClick: (() -> Unit)? = null
) {
    CenterAlignedTopAppBar(
        modifier = modifier,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge
            )
        },
        navigationIcon = {
            if (showBack) {
                IconButton(onClick = { onBackClick?.invoke() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        },
        actions = {
            if (showSearch) {
                IconButton(onClick = { onSearchClick?.invoke() }) {
                    Icon(Icons.Filled.Search, contentDescription = "Search")
                }
            }
            if (showNotifications) {
                IconButton(onClick = { onNotificationClick?.invoke() }) {
                    Icon(Icons.Filled.Notifications, contentDescription = "Notifications")
                }
            }
            if (showMore) {
                IconButton(onClick = { onMoreClick?.invoke() }) {
                    Icon(Icons.Filled.MoreVert, contentDescription = "More")
                }
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.Transparent
        )
    )
}
