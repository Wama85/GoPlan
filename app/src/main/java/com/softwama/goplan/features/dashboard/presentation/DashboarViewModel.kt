package com.softwama.goplan.features.dashboard

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class DashboardState(
    val userName: String = "Usuario",
    val isLoading: Boolean = false
)

class DashboardViewModel : ViewModel() {

    private val _state = MutableStateFlow(DashboardState())
    val state: StateFlow<DashboardState> = _state.asStateFlow()

    init {
        // Aquí puedes cargar datos del usuario si es necesario
        loadUserData()
    }

    private fun loadUserData() {
        // Por ahora dejamos datos básicos
        // Más adelante puedes cargar del repositorio
        _state.value = _state.value.copy(
            userName = "Juan Pérez" // Esto lo puedes obtener del login después
        )
    }
}