
package com.softwama.goplan.features.suscribe.domain.repository

import com.softwama.goplan.features.suscribe.domain.model.Suscribe

interface SuscribeRepository {
    suspend fun registrarUsuario(suscribe: Suscribe): Result<String>
    suspend fun checkUserExists(user: String): Boolean
    suspend fun checkEmailExists(correo: String): Boolean
}