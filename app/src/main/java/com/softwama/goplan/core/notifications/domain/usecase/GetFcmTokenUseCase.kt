package com.softwama.goplan.core.notifications.domain.usecase

import com.softwama.goplan.core.notifications.domain.repository.NotificationRepository

class GetFcmTokenUseCase(
    private val repository: NotificationRepository
) {
    suspend operator fun invoke(): Result<String> {
        return repository.getFcmToken()
    }
}