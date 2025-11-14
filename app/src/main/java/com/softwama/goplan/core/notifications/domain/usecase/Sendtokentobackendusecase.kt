package com.softwama.goplan.core.notifications.domain.usecase

import com.softwama.goplan.core.notifications.domain.repository.NotificationRepository

class SendTokenToBackendUseCase(
    private val repository: NotificationRepository
) {
    suspend operator fun invoke(token: String): Result<Unit> {
        return repository.sendTokenToBackend(token)
    }
}