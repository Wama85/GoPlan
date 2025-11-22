package com.softwama.goplan.data.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.softwama.goplan.features.tareas.domain.model.Tarea
import com.softwama.goplan.features.proyectos.domain.model.Proyecto
import com.softwama.goplan.features.proyectos.domain.model.Actividad
import kotlinx.coroutines.tasks.await

class FirestoreDataSource {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private fun getUserId(): String? = auth.currentUser?.uid

    // ========== TAREAS ==========
    suspend fun guardarTarea(tarea: Tarea): Result<Unit> {
        val userId = getUserId() ?: return Result.failure(Exception("Usuario no autenticado"))
        return try {
            firestore.collection("users")
                .document(userId)
                .collection("tareas")
                .document(tarea.id)
                .set(tarea.toMap())
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun eliminarTarea(tareaId: String): Result<Unit> {
        val userId = getUserId() ?: return Result.failure(Exception("Usuario no autenticado"))
        return try {
            firestore.collection("users")
                .document(userId)
                .collection("tareas")
                .document(tareaId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun obtenerTareas(): Result<List<Tarea>> {
        val userId = getUserId() ?: return Result.failure(Exception("Usuario no autenticado"))
        return try {
            val snapshot = firestore.collection("users")
                .document(userId)
                .collection("tareas")
                .get()
                .await()
            val tareas = snapshot.documents.mapNotNull { it.toTarea() }
            Result.success(tareas)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ========== PROYECTOS ==========
    suspend fun guardarProyecto(proyecto: Proyecto): Result<Unit> {
        val userId = getUserId() ?: return Result.failure(Exception("Usuario no autenticado"))
        return try {
            firestore.collection("users")
                .document(userId)
                .collection("proyectos")
                .document(proyecto.id)
                .set(proyecto.toMap())
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun eliminarProyecto(proyectoId: String): Result<Unit> {
        val userId = getUserId() ?: return Result.failure(Exception("Usuario no autenticado"))
        return try {
            firestore.collection("users")
                .document(userId)
                .collection("proyectos")
                .document(proyectoId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun obtenerProyectos(): Result<List<Proyecto>> {
        val userId = getUserId() ?: return Result.failure(Exception("Usuario no autenticado"))
        return try {
            val snapshot = firestore.collection("users")
                .document(userId)
                .collection("proyectos")
                .get()
                .await()
            val proyectos = snapshot.documents.mapNotNull { it.toProyecto() }
            Result.success(proyectos)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ========== ACTIVIDADES ==========
    suspend fun guardarActividad(actividad: Actividad): Result<Unit> {
        val userId = getUserId() ?: return Result.failure(Exception("Usuario no autenticado"))
        return try {
            firestore.collection("users")
                .document(userId)
                .collection("actividades")
                .document(actividad.id)
                .set(actividad.toMap())
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun eliminarActividad(actividadId: String): Result<Unit> {
        val userId = getUserId() ?: return Result.failure(Exception("Usuario no autenticado"))
        return try {
            firestore.collection("users")
                .document(userId)
                .collection("actividades")
                .document(actividadId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ========== EXTENSIONES ==========
    private fun Tarea.toMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "titulo" to titulo,
        "descripcion" to descripcion,
        "completada" to completada,
        "proyectoId" to proyectoId,
        "fechaCreacion" to fechaCreacion,
        "fechaVencimiento" to fechaVencimiento
    )

    private fun Proyecto.toMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "nombre" to nombre,
        "descripcion" to descripcion,
        "colorHex" to colorHex,
        "progreso" to progreso,
        "fechaCreacion" to fechaCreacion,
        "fechaInicio" to fechaInicio,
        "fechaFin" to fechaFin
    )

    private fun Actividad.toMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "proyectoId" to proyectoId,
        "nombre" to nombre,
        "descripcion" to descripcion,
        "completada" to completada,
        "fechaInicio" to fechaInicio,
        "fechaFin" to fechaFin
    )

    private fun com.google.firebase.firestore.DocumentSnapshot.toTarea(): Tarea? {
        return try {
            Tarea(
                id = getString("id") ?: return null,
                titulo = getString("titulo") ?: "",
                descripcion = getString("descripcion") ?: "",
                completada = getBoolean("completada") ?: false,
                proyectoId = getString("proyectoId") ?: "",
                fechaCreacion = getLong("fechaCreacion") ?: System.currentTimeMillis(),
                fechaVencimiento = getLong("fechaVencimiento")
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun com.google.firebase.firestore.DocumentSnapshot.toProyecto(): Proyecto? {
        return try {
            Proyecto(
                id = getString("id") ?: return null,
                nombre = getString("nombre") ?: "",
                descripcion = getString("descripcion") ?: "",
                colorHex = getString("colorHex") ?: "#2196F3",
                progreso = getDouble("progreso")?.toFloat() ?: 0f,
                fechaCreacion = getLong("fechaCreacion") ?: System.currentTimeMillis(),
                fechaInicio = getLong("fechaInicio") ?: System.currentTimeMillis(),
                fechaFin = getLong("fechaFin") ?: System.currentTimeMillis()
            )
        } catch (e: Exception) {
            null
        }
    }
}