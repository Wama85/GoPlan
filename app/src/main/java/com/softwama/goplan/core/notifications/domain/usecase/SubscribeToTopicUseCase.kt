package com.softwama.goplan.core.notifications.domain.usecase

import com.softwama.goplan.core.notifications.domain.repository.NotificationRepository

class SubscribeToTopicUseCase(
    private val repository: NotificationRepository
) {
    suspend operator fun invoke(topic: String): Result<Unit> {
        return repository.subscribeToTopic(topic)
    }
}