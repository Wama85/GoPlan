// features/suscribe/domain/usecase/GetSuscribeUseCase.kt
package com.softwama.goplan.features.suscribe.domain.usecase

import com.softwama.goplan.features.suscribe.domain.repository.SuscribeRepository

class GetSuscribeUseCase(
    private val repository: SuscribeRepository
) {
    suspend fun checkUserExists(user: String): Boolean {
        return try {
            repository.checkUserExists(user)
        } catch (e: Exception) {
            false
        }
    }

    suspend fun checkEmailExists(correo: String): Boolean {
        return try {
            repository.checkEmailExists(correo)
        } catch (e: Exception) {
            false
        }
    }
}