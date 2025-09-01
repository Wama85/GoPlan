package com.softwama.goplan.features.login.data

import com.softwama.goplan.features.login.domain.model.LoginRequest
import com.softwama.goplan.features.login.domain.model.LoginResponse
import com.softwama.goplan.features.login.domain.repository.LoginRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class LoginRepositoryImpl : LoginRepository {

    override fun login(request: LoginRequest): Flow<Result<LoginResponse>> = flow {
        try {
            // Simulamos un delay de red
            delay(1000)

            // Validación simple
            if (request.username.isEmpty() || request.password.isEmpty()) {
                emit(Result.failure(Exception("Usuario y contraseña son requeridos")))
                return@flow
            }

            if (request.username == "admin" && request.password == "admin") {
                val response = LoginResponse(
                    token = "mock_token_${System.currentTimeMillis()}",
                    userId = "user_123"
                )
                emit(Result.success(response))
            } else {
                emit(Result.failure(Exception("Credenciales inválidas")))
            }
        } catch (e: Exception) {
            emit(Result.failure(Exception(e.message ?: "Error de conexión")))
        }
    }
}