package com.softwama.goplan.features.tareas.data.repository

import android.util.Log
import com.softwama.goplan.data.local.database.dao.TareaDao
import com.softwama.goplan.data.local.database.entity.TareaEntity
import com.softwama.goplan.data.local.datastore.UserPreferencesDataStore
import com.softwama.goplan.data.remote.FirestoreDataSource
import com.softwama.goplan.features.tareas.domain.model.Tarea
import com.softwama.goplan.features.tareas.domain.repository.TareaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import java.util.UUID

class TareaRepositoryImpl(
    private val tareaDao: TareaDao,
    private val firestoreDataSource: FirestoreDataSource? = null,
    private val dataStore: UserPreferencesDataStore
) : TareaRepository {

    private suspend fun getUserId(): String {
        return dataStore.getUserToken().first() ?: ""
    }

    override fun obtenerTareas(): Flow<List<Tarea>> {
        return dataStore.getUserToken().flatMapLatest { userId ->
            if (userId.isNullOrEmpty()) {
                flowOf(emptyList())
            } else {
                tareaDao.obtenerTodas(userId).map { entities ->
                    entities.map { it.toDomain() }
                }
            }
        }
    }

    override suspend fun crearTarea(tarea: Tarea) {
        val userId = getUserId()
        val nuevaTarea = tarea.copy(id = UUID.randomUUID().toString())
        tareaDao.insertar(TareaEntity.fromDomain(nuevaTarea, userId))
        sincronizarConFirestore(nuevaTarea)
    }

    override suspend fun actualizarTarea(tarea: Tarea) {
        val userId = getUserId()
        tareaDao.actualizar(TareaEntity.fromDomain(tarea, userId))
        sincronizarConFirestore(tarea)
    }

    override suspend fun eliminarTarea(tareaId: String) {
        val userId = getUserId()
        tareaDao.eliminar(tareaId, userId)
        try {
            firestoreDataSource?.eliminarTarea(tareaId)
        } catch (e: Exception) {
            Log.w("TareaRepo", "Firestore no disponible")
        }
    }

    private suspend fun sincronizarConFirestore(tarea: Tarea) {
        try {
            firestoreDataSource?.guardarTarea(tarea)?.onSuccess {
                tareaDao.marcarSincronizada(tarea.id)
            }
        } catch (e: Exception) {
            Log.w("TareaRepo", "Firestore no disponible, datos guardados localmente")
        }
    }
}