package com.softwama.goplan.features.profile.domain.usecase

import com.softwama.goplan.data.local.datastore.UserPreferencesDataStore

/**
 * Guarda el estado del modo oscuro para persistir entre sesiones.
 */
class SetThemeUseCase(
    private val dataStore: UserPreferencesDataStore
) {
    suspend operator fun invoke(enabled: Boolean) {
        dataStore.setDarkMode(enabled)
    }
}
