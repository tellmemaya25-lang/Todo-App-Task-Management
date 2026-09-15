package com.sabihon.todo.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
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
    val theme: ThemePref = ThemePref.SYSTEM,
    val isLoading: Boolean = false,
    val error: String? = null,
    val showDeleteConfirm: Boolean = false
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
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
        _uiState.update {
            it.copy(
                displayName = user?.displayName ?: "",
                email = user?.email ?: "",
                photoUrl = user?.photoUrl?.toString()
            )
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
        _uiState.update { it.copy(displayName = name) }
    }

    fun saveDisplayName() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
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
            // Also update Firestore user doc
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
}
