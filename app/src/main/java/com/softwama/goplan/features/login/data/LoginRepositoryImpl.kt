package com.softwama.goplan.features.login.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.softwama.goplan.features.login.domain.repository.LoginRepository
import kotlinx.coroutines.tasks.await

class LoginRepositoryImpl : LoginRepository {

    private val auth = FirebaseAuth.getInstance()

    override suspend fun login(email: String, password: String): Result<String> {
        return try {
            Log.d("LoginRepo", "Intentando login con: $email")

            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val userId = authResult.user?.uid ?: throw Exception("Error al obtener UID")

            Log.d("LoginRepo", "Login exitoso: $userId")
            Result.success(userId)
        } catch (e: Exception) {
            Log.e("LoginRepo", "Error en login", e)
            val errorMsg = when {
                e.message?.contains("password", ignoreCase = true) == true -> "Contraseña incorrecta"
                e.message?.contains("user", ignoreCase = true) == true ||
                        e.message?.contains("email", ignoreCase = true) == true -> "Usuario no encontrado"
                e.message?.contains("network", ignoreCase = true) == true -> "Error de conexión"
                else -> "Error al iniciar sesión"
            }
            Result.failure(Exception(errorMsg))
        }
    }
}