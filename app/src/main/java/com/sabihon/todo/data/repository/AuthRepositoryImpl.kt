package com.sabihon.todo.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.sabihon.todo.core.util.Result
import com.sabihon.todo.data.remote.dto.UserDto
import com.sabihon.todo.di.IoDispatcher
import com.sabihon.todo.domain.repository.AuthRepository
import com.sabihon.todo.domain.repository.CategoryRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Firebase Auth implementation – creates users/{uid} doc and seeds default categories.
 */
@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val categoryRepository: CategoryRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : AuthRepository {

    override fun getCurrentUserId(): String? = auth.currentUser?.uid
    override fun isUserLoggedIn(): Boolean = auth.currentUser != null

    override fun observeAuthState(): Flow<Boolean> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser != null)
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    override suspend fun signUp(email: String, password: String, displayName: String): Result<String> =
        withContext(ioDispatcher) {
            try {
                val res = auth.createUserWithEmailAndPassword(email, password).await()
                val user = res.user ?: return@withContext Result.Error(Exception("User creation failed"))
                // Update display name
                val profileUpdate = UserProfileChangeRequest.Builder()
                    .setDisplayName(displayName)
                    .build()
                user.updateProfile(profileUpdate).await()

                val uid = user.uid
                // Create users/{uid} doc
                val userDto = UserDto(
                    displayName = displayName,
                    email = email,
                    photoUrl = null,
                    themePref = "SYSTEM"
                )
                firestore.collection("users").document(uid).set(userDto).await()

                // Seed default categories
                categoryRepository.seedDefaultCategories()

                Result.Success(uid)
            } catch (e: Exception) {
                Result.Error(e, friendlyMessage(e))
            }
        }

    override suspend fun signIn(email: String, password: String): Result<String> =
        withContext(ioDispatcher) {
            try {
                val res = auth.signInWithEmailAndPassword(email, password).await()
                Result.Success(res.user?.uid ?: "")
            } catch (e: Exception) {
                Result.Error(e, friendlyMessage(e))
            }
        }

    override suspend fun signOut(): Result<Unit> = withContext(ioDispatcher) {
        try {
            auth.signOut()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun sendPasswordReset(email: String): Result<Unit> = withContext(ioDispatcher) {
        try {
            auth.sendPasswordResetEmail(email).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e, friendlyMessage(e))
        }
    }

    override suspend fun deleteAccount(): Result<Unit> = withContext(ioDispatcher) {
        try {
            val uid = auth.currentUser?.uid ?: throw IllegalStateException("Not logged in")
            // Delete user subcollections would require cloud function; for now delete user doc
            firestore.collection("users").document(uid).delete().await()
            auth.currentUser?.delete()?.await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e, friendlyMessage(e))
        }
    }

    private fun friendlyMessage(e: Exception): String {
        val msg = e.message?.lowercase() ?: ""
        return when {
            "network" in msg -> "No internet connection. Please check your network."
            "invalid-email" in msg || "badly formatted" in msg -> "Invalid email format."
            "user-not-found" in msg -> "No account found with this email."
            "wrong-password" in msg || "invalid-credential" in msg -> "Incorrect email or password."
            "email-already-in-use" in msg -> "Email already in use."
            "weak-password" in msg -> "Password is too weak. Use at least 6 characters."
            "too-many-requests" in msg -> "Too many attempts. Please try again later."
            else -> e.localizedMessage ?: "Something went wrong. Please try again."
        }
    }
}
