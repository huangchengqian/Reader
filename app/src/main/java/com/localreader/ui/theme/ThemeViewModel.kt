package com.localreader.ui.theme

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

data class ThemeUiState(
    val isDarkTheme: Boolean = false,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
)

enum class ThemeMode {
    LIGHT, DARK, SYSTEM
}

class ThemeViewModel(
    private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(ThemeUiState())
    val uiState: StateFlow<ThemeUiState> = _uiState.asStateFlow()

    private val themeModeKey = stringPreferencesKey("theme_mode")

    init {
        viewModelScope.launch {
            context.dataStore.data.map { preferences ->
                preferences[themeModeKey]?.let {
                    try { ThemeMode.valueOf(it) } catch (e: Exception) { ThemeMode.SYSTEM }
                } ?: ThemeMode.SYSTEM
            }.collect { mode ->
                val isDark = when (mode) {
                    ThemeMode.DARK -> true
                    ThemeMode.LIGHT -> false
                    ThemeMode.SYSTEM -> null // indicates system default
                }
                _uiState.update { it.copy(themeMode = mode, isDarkTheme = isDark ?: false) }
            }
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            context.dataStore.edit { preferences ->
                preferences[themeModeKey] = mode.name
            }
        }
    }

    companion object {
        fun factory(context: Context): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ThemeViewModel(context.applicationContext) as T
                }
            }
        }
    }
}
