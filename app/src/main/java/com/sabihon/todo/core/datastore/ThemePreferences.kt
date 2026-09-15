package com.sabihon.todo.core.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "sabihon_prefs")

enum class ThemePref { SYSTEM, LIGHT, DARK }

@Singleton
class ThemePreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val themeKey = stringPreferencesKey("theme_pref")

    val themeFlow: Flow<ThemePref> = context.dataStore.data.map { prefs ->
        val value = prefs[themeKey] ?: ThemePref.SYSTEM.name
        try {
            ThemePref.valueOf(value)
        } catch (e: Exception) {
            ThemePref.SYSTEM
        }
    }

    suspend fun setTheme(theme: ThemePref) {
        context.dataStore.edit { prefs ->
            prefs[themeKey] = theme.name
        }
    }
}
