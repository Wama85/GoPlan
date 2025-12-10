package com.softwama.goplan.features.maintenance.presentation
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softwama.goplan.features.maintenance.domain.CheckMaintenanceUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MaintenanceViewModel(
    private val checkMaintenanceUseCase: CheckMaintenanceUseCase
) : ViewModel() {

    private val _isMaintenance = MutableStateFlow<Boolean?>(null)
    val isMaintenance: StateFlow<Boolean?> get() = _isMaintenance

    private var pollingJob: Job? = null

    fun checkAppStatus() {
        viewModelScope.launch {
            _isMaintenance.value = checkMaintenanceUseCase()
        }
    }

    fun startPolling() {
        pollingJob?.cancel()
        pollingJob = viewModelScope.launch {
            while (true) {
                _isMaintenance.value = checkMaintenanceUseCase()
                delay(30_000)
            }
        }
    }

    fun stopPolling() {
        pollingJob?.cancel()
        pollingJob = null
    }

    override fun onCleared() {
        super.onCleared()
        stopPolling()
    }
}