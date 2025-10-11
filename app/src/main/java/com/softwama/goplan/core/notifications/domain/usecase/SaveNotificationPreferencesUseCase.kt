package com.softwama.goplan.core.notifications.domain.usecase

import com.softwama.goplan.data.local.datastore.UserPreferencesDataStore


class SaveNotificationPreferencesUseCase(
    private val userPreferences: UserPreferencesDataStore
) {
    suspend operator fun invoke(enabled: Boolean, time: String) {
        userPreferences.saveNotificationPreferences(enabled, time)
    }
}