package com.softwama.goplan.features.login.data

import com.softwama.goplan.features.login.domain.model.LoginRequest
import com.softwama.goplan.features.login.domain.model.LoginResponse
import com.softwama.goplan.features.login.domain.repository.LoginRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.UUID

class LoginRepositoryImpl : LoginRepository {

    override fun login(request: LoginRequest): Flow<Result<LoginResponse>> = flow {
        // Simulamos una llamada a la red
        delay(1000)

        // Validación simple (en producción esto viene del servidor)
        if (request.username.isNotBlank() && request.password.length >= 4) {
            // Login exitoso - generamos un token simulado
            val token = "token_${UUID.randomUUID()}"

            emit(Result.success(
                LoginResponse(
                    success = true,
                    message = "Inicio de sesión exitoso",
                    token = token,
                    userName = request.username,
                    userEmail = "${request.username}@goplan.com" // Email simulado
                )
            ))
        } else {
            // Login fallido
            emit(Result.failure(
                Exception("Usuario o contraseña incorrectos")
            ))
        }
    }
}