package com.softwama.goplan.features.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softwama.goplan.features.profile.domain.usecase.GetThemeUseCase
import com.softwama.goplan.features.profile.domain.usecase.SetThemeUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SettingsState(
    val isDarkMode: Boolean = false
)

class SettingsViewModel(
    private val getThemeUseCase: GetThemeUseCase,
    private val setThemeUseCase: SetThemeUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    init {
        loadTheme()
    }

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
}
