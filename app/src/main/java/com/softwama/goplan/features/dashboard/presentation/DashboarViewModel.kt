package com.softwama.goplan.features.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softwama.goplan.data.local.datastore.UserPreferencesDataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DashboardState(
    val userName: String = "",
    val isLoading: Boolean = true
)

class DashboardViewModel(
    private val dataStore: UserPreferencesDataStore
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardState())
    val state: StateFlow<DashboardState> = _state.asStateFlow()

    init {
        loadUserData()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            dataStore.getUserName().collect { name ->
                _state.value = _state.value.copy(
                    userName = name.ifEmpty { "Usuario" },
                    isLoading = false
                )
            }
        }
    }
}