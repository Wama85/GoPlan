package com.softwama.goplan.features.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softwama.goplan.data.local.datastore.UserPreferencesDataStore
import com.softwama.goplan.features.profile.domain.usecase.GetThemeUseCase
import com.softwama.goplan.features.profile.domain.usecase.SetThemeUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch

data class SettingsState(
    val isDarkMode: Boolean = false,
    val language: String = "es"   // 👈 agregamos el idioma al estado
)

class SettingsViewModel(
    private val getThemeUseCase: GetThemeUseCase,
    private val setThemeUseCase: SetThemeUseCase,
    private val dataStore: UserPreferencesDataStore   // 👈 necesario para idioma
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    // 👇 Flujo del idioma desde DataStore (lo verá SettingsScreen)
    val language = dataStore.getLanguage()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            "es"
        )

    init {
        loadTheme()
        loadLanguage()
    }

    // =============== TEMA OSCURO ===============
    private fun loadTheme() {
        viewModelScope.launch {
            getThemeUseCase().collect { enabled ->
                _state.value = _state.value.copy(isDarkMode = enabled)
            }
        }
    }

    fun toggleTheme(enabled: Boolean) {
        viewModelScope.launch {
            setThemeUseCase(enabled)
            _state.value = _state.value.copy(isDarkMode = enabled)
        }
    }

    // =============== IDIOMA ===============
    private fun loadLanguage() {
        viewModelScope.launch {
            dataStore.getLanguage().collect { lang ->
                _state.value = _state.value.copy(language = lang)
            }
        }
    }

    fun changeLanguage(lang: String) {
        viewModelScope.launch {
            dataStore.saveLanguage(lang)
            _state.value = _state.value.copy(language = lang)
        }
    }
}
