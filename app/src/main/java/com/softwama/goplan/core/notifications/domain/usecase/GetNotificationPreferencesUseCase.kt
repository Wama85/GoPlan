package com.softwama.goplan.core.notifications.domain.usecase

import com.softwama.goplan.data.local.datastore.UserPreferencesDataStore
import kotlinx.coroutines.flow.Flow

data class NotificationPreferences(
    val enabled: Boolean = true,
    val timeMinutes: String = "15"
)

class GetNotificationPreferencesUseCase(
    private val userPreferences: UserPreferencesDataStore
) {
    operator fun invoke(): Flow<NotificationPreferences> {
        return userPreferences.getNotificationPreferences()
    }
}