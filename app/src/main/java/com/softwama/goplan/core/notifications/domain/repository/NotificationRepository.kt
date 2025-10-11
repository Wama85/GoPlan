package com.softwama.goplan.core.notifications.domain.repository

interface NotificationRepository {
    suspend fun getFcmToken(): Result<String>
    suspend fun subscribeToTopic(topic: String): Result<Unit>
    suspend fun unsubscribeFromTopic(topic: String): Result<Unit>
    suspend fun sendTokenToBackend(token: String): Result<Unit>
}