package com.sabihon.todo.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Provides Firebase instances. Firestore offline persistence enabled.
 * Handles case where Firestore already initialized to avoid IllegalStateException.
 */
@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore {
        val firestore = FirebaseFirestore.getInstance()
        try {
            // Only set if not already set – otherwise throws IllegalStateException
            if (!firestore.firestoreSettings.isPersistenceEnabled) {
                val settings = FirebaseFirestoreSettings.Builder()
                    .setPersistenceEnabled(true)
                    .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
                    .build()
                firestore.firestoreSettings = settings
            }
        } catch (e: Exception) {
            // Settings already set or Firestore already used – ignore, log
            android.util.Log.w("FirebaseModule", "Firestore settings already set, skipping: ${e.message}")
        }
        return firestore
    }

    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage = FirebaseStorage.getInstance()
}
