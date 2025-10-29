
package com.softwama.goplan.features.login.domain.usecase

import com.softwama.goplan.features.login.domain.repository.LoginRepository

class LoginUseCase(
    private val repository: LoginRepository
) {
    suspend operator fun invoke(emailOrUsername: String, password: String): Result<String> {
        if (emailOrUsername.isBlank()) {
            return Result.failure(Exception("El correo es requerido"))
        }
        if (password.isBlank()) {
            return Result.failure(Exception("La contraseña es requerida"))
        }

        // Si no contiene @, asumir que es username y agregar @goplan.com
        val email = if (emailOrUsername.contains("@")) {
            emailOrUsername
        } else {
            "${emailOrUsername}@goplan.com"
        }

        return repository.login(email, password)
    }
}