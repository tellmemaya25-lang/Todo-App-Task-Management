package com.sabihon.todo.ui.profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.sabihon.todo.core.datastore.ThemePreferences
import com.sabihon.todo.core.datastore.ThemePref
import com.sabihon.todo.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

data class ProfileUiState(
    val displayName: String = "",
    val email: String = "",
    val photoUrl: String? = null,
    val localPhotoUri: Uri? = null, // For newly picked image before upload
    val theme: ThemePref = ThemePref.SYSTEM,
    val isLoading: Boolean = false,
    val error: String? = null,
    val showDeleteConfirm: Boolean = false,
    val isUploadingPhoto: Boolean = false
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val authRepository: AuthRepository,
    private val themePreferences: ThemePreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
        observeTheme()
    }

    private fun loadProfile() {
        val user = auth.currentUser
        viewModelScope.launch {
            try {
                val uid = user?.uid
                if (uid != null) {
                    val doc = firestore.collection("users").document(uid).get().await()
                    val firestorePhoto = doc.getString("photoUrl")
                    val firestoreName = doc.getString("displayName")
                    _uiState.update {
                        it.copy(
                            displayName = firestoreName ?: user?.displayName ?: "",
                            email = user?.email ?: "",
                            photoUrl = firestorePhoto ?: user?.photoUrl?.toString()
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            displayName = user?.displayName ?: "",
                            email = user?.email ?: "",
                            photoUrl = user?.photoUrl?.toString()
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        displayName = user?.displayName ?: "",
                        email = user?.email ?: "",
                        photoUrl = user?.photoUrl?.toString(),
                        error = e.message
                    )
                }
            }
        }
    }

    private fun observeTheme() {
        viewModelScope.launch {
            themePreferences.themeFlow.collectLatest { theme ->
                _uiState.update { it.copy(theme = theme) }
            }
        }
    }

    fun onDisplayNameChange(name: String) {
        _uiState.update { it.copy(displayName = name, error = null) }
    }

    fun onPhotoPicked(uri: Uri) {
        _uiState.update { it.copy(localPhotoUri = uri, error = null) }
        uploadPhoto(uri)
    }

    private fun uploadPhoto(uri: Uri) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUploadingPhoto = true, error = null) }
            try {
                val uid = auth.currentUser?.uid ?: throw IllegalStateException("Not authenticated")
                val storageRef = storage.reference.child("users/$uid/profile_${System.currentTimeMillis()}.jpg")
                storageRef.putFile(uri).await()
                val downloadUrl = storageRef.downloadUrl.await().toString()

                // Update Firebase Auth profile
                val user = auth.currentUser
                val profileUpdate = UserProfileChangeRequest.Builder()
                    .setPhotoUri(Uri.parse(downloadUrl))
                    .build()
                user?.updateProfile(profileUpdate)?.await()

                // Update Firestore
                firestore.collection("users").document(uid).update(
                    mapOf("photoUrl" to downloadUrl)
                ).await()

                _uiState.update {
                    it.copy(
                        photoUrl = downloadUrl,
                        localPhotoUri = null,
                        isUploadingPhoto = false
                    )
                }
            } catch (e: Exception) {
                // Fallback: keep local URI if upload fails (offline), still show image
                _uiState.update {
                    it.copy(
                        isUploadingPhoto = false,
                        error = "Photo upload failed: ${e.message}. Showing locally."
                    )
                }
            }
        }
    }

    fun saveDisplayName() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val user = auth.currentUser ?: return@launch
                val update = UserProfileChangeRequest.Builder()
                    .setDisplayName(_uiState.value.displayName)
                    .build()
                user.updateProfile(update).await()
                firestore.collection("users").document(user.uid)
                    .update("displayName", _uiState.value.displayName).await()
                _uiState.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun setTheme(theme: ThemePref) {
        viewModelScope.launch {
            themePreferences.setTheme(theme)
            try {
                val uid = auth.currentUser?.uid ?: return@launch
                firestore.collection("users").document(uid)
                    .update("themePref", theme.name).await()
            } catch (_: Exception) {}
        }
    }

    fun signOut(onDone: () -> Unit) {
        viewModelScope.launch {
            authRepository.signOut()
            onDone()
        }
    }

    fun setShowDeleteConfirm(show: Boolean) {
        _uiState.update { it.copy(showDeleteConfirm = show) }
    }

    fun deleteAccount(onDone: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = authRepository.deleteAccount()) {
                is com.sabihon.todo.core.util.Result.Success -> {
                    _uiState.update { it.copy(isLoading = false) }
                    onDone()
                }
                is com.sabihon.todo.core.util.Result.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = result.message) }
                }
                else -> {}
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
