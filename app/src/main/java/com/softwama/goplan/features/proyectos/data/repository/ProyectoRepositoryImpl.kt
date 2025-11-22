package com.softwama.goplan.features.proyectos.data.repository

import android.util.Log
import com.softwama.goplan.data.local.database.dao.ProyectoDao
import com.softwama.goplan.data.local.database.entity.ProyectoEntity
import com.softwama.goplan.data.remote.FirestoreDataSource
import com.softwama.goplan.features.proyectos.domain.model.Proyecto
import com.softwama.goplan.features.proyectos.domain.repository.ProyectoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class ProyectoRepositoryImpl(
    private val proyectoDao: ProyectoDao,
    private val firestoreDataSource: FirestoreDataSource? = null
) : ProyectoRepository {

    override fun obtenerProyectos(): Flow<List<Proyecto>> {
        return proyectoDao.obtenerTodos().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun crearProyecto(proyecto: Proyecto) {
        val nuevoProyecto = proyecto.copy(id = UUID.randomUUID().toString())
        proyectoDao.insertar(ProyectoEntity.fromDomain(nuevoProyecto))
        sincronizarConFirestore(nuevoProyecto)
    }

    override suspend fun actualizarProyecto(proyecto: Proyecto) {
        proyectoDao.actualizar(ProyectoEntity.fromDomain(proyecto))
        sincronizarConFirestore(proyecto)
    }

    override suspend fun eliminarProyecto(proyectoId: String) {
        proyectoDao.eliminar(proyectoId)
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