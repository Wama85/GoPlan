package com.softwama.goplan.features.proyectos.data.repository

import android.util.Log
import com.softwama.goplan.data.local.database.dao.ProyectoDao
import com.softwama.goplan.data.local.database.entity.ProyectoEntity
import com.softwama.goplan.data.local.datastore.UserPreferencesDataStore
import com.softwama.goplan.data.remote.FirestoreDataSource
import com.softwama.goplan.features.proyectos.domain.model.Proyecto
import com.softwama.goplan.features.proyectos.domain.repository.ProyectoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import java.util.UUID

class ProyectoRepositoryImpl(
    private val proyectoDao: ProyectoDao,
    private val firestoreDataSource: FirestoreDataSource? = null,
    private val dataStore: UserPreferencesDataStore
) : ProyectoRepository {

    private suspend fun getUserId(): String {
        return dataStore.getUserToken().first() ?: ""
    }

    override fun obtenerProyectos(): Flow<List<Proyecto>> {
        return dataStore.getUserToken().flatMapLatest { userId ->
            if (userId.isNullOrEmpty()) {
                flowOf(emptyList())
            } else {
                proyectoDao.obtenerTodos(userId).map { entities ->
                    entities.map { it.toDomain() }
                }
            }
        }
    }

    override suspend fun crearProyecto(proyecto: Proyecto) {
        val userId = getUserId()
        val nuevoProyecto = proyecto.copy(id = UUID.randomUUID().toString())
        proyectoDao.insertar(ProyectoEntity.fromDomain(nuevoProyecto, userId))
        sincronizarConFirestore(nuevoProyecto)
    }

    override suspend fun actualizarProyecto(proyecto: Proyecto) {
        val userId = getUserId()
        proyectoDao.actualizar(ProyectoEntity.fromDomain(proyecto, userId))
        sincronizarConFirestore(proyecto)
    }

    override suspend fun eliminarProyecto(proyectoId: String) {
        val userId = getUserId()
        proyectoDao.eliminar(proyectoId, userId)
        try {
            firestoreDataSource?.eliminarProyecto(proyectoId)
        } catch (e: Exception) {
            Log.w("ProyectoRepo", "Firestore no disponible")
        }
    }

    private suspend fun sincronizarConFirestore(proyecto: Proyecto) {
        try {
            firestoreDataSource?.guardarProyecto(proyecto)?.onSuccess {
                proyectoDao.marcarSincronizado(proyecto.id)
            }
        } catch (e: Exception) {
            Log.w("ProyectoRepo", "Firestore no disponible, datos guardados localmente")
        }
    }
}