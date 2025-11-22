package com.softwama.goplan.data.local.database.dao

import androidx.room.*
import com.softwama.goplan.data.local.database.entity.ActividadEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ActividadDao {
    @Query("SELECT * FROM actividades WHERE proyectoId = :proyectoId ORDER BY fechaInicio ASC")
    fun obtenerPorProyecto(proyectoId: String): Flow<List<ActividadEntity>>

    @Query("SELECT * FROM actividades WHERE id = :id")
    suspend fun obtenerPorId(id: String): ActividadEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(actividad: ActividadEntity)

    @Update
    suspend fun actualizar(actividad: ActividadEntity)

    @Query("DELETE FROM actividades WHERE id = :id")
    suspend fun eliminar(id: String)

    @Query("SELECT * FROM actividades WHERE sincronizado = 0")
    suspend fun obtenerNoSincronizadas(): List<ActividadEntity>

    @Query("UPDATE actividades SET sincronizado = 1 WHERE id = :id")
    suspend fun marcarSincronizada(id: String)
}