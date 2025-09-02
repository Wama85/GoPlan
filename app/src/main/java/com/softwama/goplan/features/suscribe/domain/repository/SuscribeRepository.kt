package com.softwama.goplan.features.suscribe.domain.repository

import com.softwama.goplan.features.suscribe.domain.model.Suscribe
import kotlinx.coroutines.flow.Flow

interface SuscribeRepository {
    suspend fun saveUser(suscribe: Suscribe): Boolean
    suspend fun checkUserExists(user: String): Boolean
    suspend fun checkEmailExists(correo: String): Boolean
}