package com.sabihon.todo.ui.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.sabihon.todo.core.datastore.ThemePref
import com.sabihon.todo.core.ui.components.ConfirmDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBack: () -> Unit = {},
    onSignOut: () -> Unit = {},
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Image picker launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.onPhotoPicked(uri)
        }
    }

    val fallbackPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.onPhotoPicked(uri)
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Avatar with edit capability – moved profile icon here as requested
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .align(Alignment.CenterHorizontally),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .clickable {
                            try {
                                photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                            } catch (e: Exception) {
                                fallbackPickerLauncher.launch("image/*")
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        uiState.isUploadingPhoto -> {
                            CircularProgressIndicator(modifier = Modifier.size(40.dp))
                        }
                        uiState.localPhotoUri != null -> {
                            AsyncImage(
                                model = uiState.localPhotoUri,
                                contentDescription = "Profile photo",
                                modifier = Modifier.fillMaxSize().clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        }
                        uiState.photoUrl != null -> {
                            AsyncImage(
                                model = uiState.photoUrl,
                                contentDescription = "Profile photo",
                                modifier = Modifier.fillMaxSize().clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        }
                        else -> {
                            Text(
                                text = uiState.displayName.take(2).uppercase().ifBlank { "U" },
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
                // Camera edit icon overlay
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .align(Alignment.BottomEnd)
                        .clickable {
                            try {
                                photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                            } catch (e: Exception) {
                                fallbackPickerLauncher.launch("image/*")
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.CameraAlt,
                        contentDescription = "Edit photo",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Text(
                "Tap photo to edit",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            OutlinedTextField(
                value = uiState.displayName,
                onValueChange = viewModel::onDisplayNameChange,
                label = { Text("Display Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
            )
            Button(
                onClick = { viewModel.saveDisplayName() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
            ) {
                if (uiState.isLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp))
                else Text("Save Name")
            }

            OutlinedTextField(
                value = uiState.email,
                onValueChange = {},
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                singleLine = true,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
            )

            Spacer(Modifier.height(8.dp))
            Text("Theme", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = uiState.theme == ThemePref.SYSTEM,
                    onClick = { viewModel.setTheme(ThemePref.SYSTEM) },
                    label = { Text("System") }
                )
                FilterChip(
                    selected = uiState.theme == ThemePref.LIGHT,
                    onClick = { viewModel.setTheme(ThemePref.LIGHT) },
                    label = { Text("Light") }
                )
                FilterChip(
                    selected = uiState.theme == ThemePref.DARK,
                    onClick = { viewModel.setTheme(ThemePref.DARK) },
                    label = { Text("Dark") }
                )
            }

            Spacer(Modifier.height(16.dp))
            OutlinedButton(onClick = { viewModel.signOut(onSignOut) }, modifier = Modifier.fillMaxWidth(), shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)) {
                Text("Sign Out")
            }
            Button(
                onClick = { viewModel.setShowDeleteConfirm(true) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
            ) {
                Text("Delete Account")
            }
        }

        if (uiState.showDeleteConfirm) {
            ConfirmDialog(
                title = "Delete Account",
                message = "This will permanently delete your account and all data. This cannot be undone.",
                confirmText = "Delete",
                onConfirm = {
                    viewModel.setShowDeleteConfirm(false)
                    viewModel.deleteAccount(onSignOut)
                },
                onDismiss = { viewModel.setShowDeleteConfirm(false) }
            )
        }
    }
}
