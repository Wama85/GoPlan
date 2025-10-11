package com.softwama.goplan.features.maintenance.domain

import com.softwama.goplan.core.remoteconfig.RemoteConfigRepository

class CheckMaintenanceUseCase(
    private val repository: RemoteConfigRepository
) {
    suspend operator fun invoke(): Boolean {
        return repository.fetchMaintenanceStatus()
    }
}
