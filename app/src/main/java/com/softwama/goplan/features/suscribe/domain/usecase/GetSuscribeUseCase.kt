package com.softwama.goplan.features.suscribe.domain.usecase


import com.softwama.goplan.features.suscribe.domain.model.Suscribe
import com.softwama.goplan.features.suscribe.domain.repository.SuscribeRepository


class GetSuscribeUseCase(
    private val repository: SuscribeRepository
) {
    suspend operator fun invoke(suscribe: Suscribe): Boolean {
        return repository.saveUser(suscribe)
    }

    suspend fun checkUserExists(user: String): Boolean {
        return repository.checkUserExists(user)
    }

    suspend fun checkEmailExists(correo: String): Boolean {
        return repository.checkEmailExists(correo)
    }
}