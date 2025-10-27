package com.softwama.goplan.features.profile.domain.usecase

import com.softwama.goplan.data.local.datastore.UserPreferencesDataStore

/**
 * Caso de uso que actualiza los datos del usuario localmente.
 * Adaptado para evitar errores de nullabilidad.
 */
class UpdateProfileUseCase(
    private val dataStore: UserPreferencesDataStore
) {
    suspend operator fun invoke(name: String?, email: String?, password: String?) {
        // Aseguramos que nunca se guarde null
        dataStore.saveUserName(name ?: "")
        dataStore.saveUserEmail(email ?: "")
        if (!password.isNullOrBlank()) {
            dataStore.savePassword(password)
        }
    }
}
