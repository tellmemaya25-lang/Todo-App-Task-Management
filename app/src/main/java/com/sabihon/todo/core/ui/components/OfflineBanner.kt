package com.sabihon.todo.core.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Offline banner – shows when no network.
 */
@Composable
fun OfflineBanner(
    isOffline: Boolean,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(visible = isOffline, modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFEB5757))
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No internet – offline mode",
                color = Color.White,
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}
