package com.softwama.goplan.features.proyectos.data.repository

import android.util.Log
import com.softwama.goplan.data.local.database.dao.ActividadDao
import com.softwama.goplan.data.local.database.entity.ActividadEntity
import com.softwama.goplan.data.local.datastore.UserPreferencesDataStore
import com.softwama.goplan.data.remote.FirestoreDataSource
import com.softwama.goplan.features.proyectos.domain.model.Actividad
import com.softwama.goplan.features.proyectos.domain.repository.ActividadRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import java.util.UUID

class ActividadRepositoryImpl(
    private val actividadDao: ActividadDao,
    private val firestoreDataSource: FirestoreDataSource? = null,
    private val dataStore: UserPreferencesDataStore
) : ActividadRepository {

    private suspend fun getUserId(): String {
        return dataStore.getUserToken().first() ?: ""
    }

    override fun obtenerActividadesPorProyecto(proyectoId: String): Flow<List<Actividad>> {
        return dataStore.getUserToken().flatMapLatest { userId ->
            if (userId.isNullOrEmpty()) {
                flowOf(emptyList())
            } else {
                actividadDao.obtenerPorProyecto(proyectoId, userId).map { entities ->
                    entities.map { it.toDomain() }
                }
            }
        }
    }

    override suspend fun crearActividad(actividad: Actividad) {
        val userId = getUserId()
        val nuevaActividad = actividad.copy(id = UUID.randomUUID().toString())
        actividadDao.insertar(ActividadEntity.fromDomain(nuevaActividad, userId))
        sincronizarConFirestore(nuevaActividad)
    }

    override suspend fun actualizarActividad(actividad: Actividad) {
        val userId = getUserId()
        actividadDao.actualizar(ActividadEntity.fromDomain(actividad, userId))
        sincronizarConFirestore(actividad)
    }

    override suspend fun eliminarActividad(actividadId: String) {
        val userId = getUserId()
        actividadDao.eliminar(actividadId, userId)
        try {
            firestoreDataSource?.eliminarActividad(actividadId)
        } catch (e: Exception) {
            Log.w("ActividadRepo", "Firestore no disponible")
        }
    }

    private suspend fun sincronizarConFirestore(actividad: Actividad) {
        try {
            firestoreDataSource?.guardarActividad(actividad)?.onSuccess {
                actividadDao.marcarSincronizada(actividad.id)
            }
        } catch (e: Exception) {
            Log.w("ActividadRepo", "Firestore no disponible, datos guardados localmente")
        }
    }
}