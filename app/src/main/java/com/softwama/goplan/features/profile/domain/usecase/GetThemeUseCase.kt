package com.softwama.goplan.features.profile.domain.usecase

import com.softwama.goplan.data.local.datastore.UserPreferencesDataStore

/**
 * Devuelve el estado actual del modo oscuro (Flow<Boolean>).
 */
class GetThemeUseCase(
    private val dataStore: UserPreferencesDataStore
) {
    operator fun invoke() = dataStore.isDarkMode()
}
