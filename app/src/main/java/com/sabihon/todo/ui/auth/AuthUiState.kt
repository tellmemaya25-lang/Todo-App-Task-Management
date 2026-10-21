package com.sabihon.todo.ui.auth

/**
 * Immutable UiState per screen.
 */
data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val displayName: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val displayNameError: String? = null,
    val confirmPasswordError: String? = null,
    val isLoggedIn: Boolean = false
)
