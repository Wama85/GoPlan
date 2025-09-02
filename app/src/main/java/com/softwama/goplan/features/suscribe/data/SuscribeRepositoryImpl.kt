package com.softwama.goplan.features.suscribe.data

import com.softwama.goplan.features.suscribe.domain.model.Suscribe
import com.softwama.goplan.features.suscribe.domain.repository.SuscribeRepository

class SuscribeRepositoryImpl : SuscribeRepository {

    // Simulación de base de datos en memoria (luego reemplazar con Room o API)
    private val users = mutableListOf<Suscribe>()

    override suspend fun saveUser(suscribe: Suscribe): Boolean {
        return try {
            // Validar que las contraseñas coincidan
            if (suscribe.pass != suscribe.repitPass) {
                return false
            }

            // Validar que el usuario no exista
            if (checkUserExists(suscribe.user)) {
                return false
            }

            // Validar que el email no exista
            if (checkEmailExists(suscribe.correo)) {
                return false
            }

            // Guardar usuario (aquí iría tu lógica de base de datos real)
            users.add(suscribe)
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun checkUserExists(user: String): Boolean {
        return users.any { it.user == user }
    }

    override suspend fun checkEmailExists(correo: String): Boolean {
        return users.any { it.correo == correo }
    }

    // Método adicional para testing o debug
    fun getUsers(): List<Suscribe> {
        return users.toList()
    }
}