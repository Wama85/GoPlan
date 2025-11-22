package com.softwama.goplan.features.tareas.data.repository

import android.util.Log
import com.softwama.goplan.data.local.database.dao.TareaDao
import com.softwama.goplan.data.local.database.entity.TareaEntity
import com.softwama.goplan.data.remote.FirestoreDataSource
import com.softwama.goplan.features.tareas.domain.model.Tarea
import com.softwama.goplan.features.tareas.domain.repository.TareaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class TareaRepositoryImpl(
    private val tareaDao: TareaDao,
    private val firestoreDataSource: FirestoreDataSource? = null
) : TareaRepository {

    override fun obtenerTareas(): Flow<List<Tarea>> {
        return tareaDao.obtenerTodas().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun crearTarea(tarea: Tarea) {
        val nuevaTarea = tarea.copy(id = UUID.randomUUID().toString())
        tareaDao.insertar(TareaEntity.fromDomain(nuevaTarea))
        sincronizarConFirestore(nuevaTarea)
    }

    override suspend fun actualizarTarea(tarea: Tarea) {
        tareaDao.actualizar(TareaEntity.fromDomain(tarea))
        sincronizarConFirestore(tarea)
    }

    override suspend fun eliminarTarea(tareaId: String) {
        tareaDao.eliminar(tareaId)
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