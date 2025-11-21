package com.softwama.goplan.features.suscribe.data

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.softwama.goplan.features.suscribe.domain.model.Suscribe
import com.softwama.goplan.features.suscribe.domain.repository.SuscribeRepository
import kotlinx.coroutines.tasks.await

class SuscribeRepositoryImpl : SuscribeRepository {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    override suspend fun registrarUsuario(suscribe: Suscribe): Result<String> {
        return try {
            Log.d("SuscribeRepo", "Iniciando registro con Auth...")

            // 1. Crear usuario en Firebase Auth (ESTO SIEMPRE FUNCIONA)
            val authResult = auth.createUserWithEmailAndPassword(
                suscribe.correo,
                suscribe.pass
            ).await()

            val userId = authResult.user?.uid ?: throw Exception("Error al obtener UID")
            Log.d("SuscribeRepo", "Usuario creado en Auth: $userId")

            // 2. Intentar guardar en Firestore (NO BLOQUEA SI FALLA)
            try {
                val userData = hashMapOf(
                    "nombre" to suscribe.nombre,
                    "apellido" to suscribe.apellido,
                    "correo" to suscribe.correo,


                    "fechaRegistro" to System.currentTimeMillis()
                )

                firestore.collection("usuarios")
                    .document(userId)
                    .set(userData)
                    .await()

                Log.d("SuscribeRepo", "Datos guardados en Firestore exitosamente")
            } catch (e: Exception) {
                // Firestore falló pero el usuario YA ESTÁ CREADO en Auth
                Log.w("SuscribeRepo", "Firestore falló pero usuario registrado en Auth: $userId", e)
            }

            Result.success("Usuario registrado exitosamente")
        } catch (e: Exception) {
            Log.e("SuscribeRepo", "Error en Auth", e)
            Result.failure(e)
        }
    }

    override suspend fun checkUserExists(user: String): Boolean {
        return try {
            val result = firestore.collection("usuarios")
                .whereEqualTo("user", user)
                .get()
                .await()
            !result.isEmpty
        } catch (e: Exception) {
            Log.e("SuscribeRepo", "Error verificando usuario", e)
            false
        }
    }

    override suspend fun checkEmailExists(correo: String): Boolean {
        return try {
            val methods = auth.fetchSignInMethodsForEmail(correo).await()
            methods.signInMethods?.isNotEmpty() ?: false
        } catch (e: Exception) {
            Log.e("SuscribeRepo", "Error verificando correo", e)
            false
        }
    }
}