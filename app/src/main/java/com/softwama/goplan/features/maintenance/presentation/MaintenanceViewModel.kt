package com.softwama.goplan.features.maintenance.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softwama.goplan.features.maintenance.domain.CheckMaintenanceUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MaintenanceViewModel(
    private val checkMaintenanceUseCase: CheckMaintenanceUseCase
) : ViewModel() {

    private val _isMaintenance = MutableStateFlow<Boolean?>(null)
    val isMaintenance: StateFlow<Boolean?> get() = _isMaintenance

    fun checkAppStatus() {
        viewModelScope.launch {
            _isMaintenance.value = checkMaintenanceUseCase()
        }
    }
}
