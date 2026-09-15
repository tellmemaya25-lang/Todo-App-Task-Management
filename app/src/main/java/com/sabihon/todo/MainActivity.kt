package com.sabihon.todo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.sabihon.todo.core.ui.theme.SabihonTheme
import com.sabihon.todo.ui.navigation.SabihonNavHost
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main launcher activity – hosts Compose NavHost.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SabihonTheme {
                SabihonNavHost()
            }
        }
    }
}
