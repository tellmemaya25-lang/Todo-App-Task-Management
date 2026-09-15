package com.sabihon.todo

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.sabihon.todo.core.ui.theme.SabihonTheme
import com.sabihon.todo.ui.navigation.SabihonNavHost
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main launcher activity – hosts Compose NavHost.
 * Handles POST_NOTIFICATIONS permission for API 33+.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ -> }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Request notification permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        // Handle deep link from notification
        val taskIdFromNotification = intent.getStringExtra("taskId")

        setContent {
            SabihonTheme {
                SabihonNavHost(
                    // If notification opened, start destination could be task detail
                    // For simplicity, we pass via intent handling inside NavHost if needed
                )
            }
        }
    }
}
