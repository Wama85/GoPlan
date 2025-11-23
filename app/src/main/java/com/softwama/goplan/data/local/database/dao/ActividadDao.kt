package com.softwama.goplan.data.local.database.dao

import androidx.room.*
import com.softwama.goplan.data.local.database.entity.ActividadEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ActividadDao {
    @Query("SELECT * FROM actividades WHERE proyectoId = :proyectoId AND userId = :userId ORDER BY fechaInicio ASC")
    fun obtenerPorProyecto(proyectoId: String, userId: String): Flow<List<ActividadEntity>>

    @Query("SELECT * FROM actividades WHERE id = :id AND userId = :userId")
    suspend fun obtenerPorId(id: String, userId: String): ActividadEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(actividad: ActividadEntity)

    @Update
    suspend fun actualizar(actividad: ActividadEntity)

    @Query("DELETE FROM actividades WHERE id = :id AND userId = :userId")
    suspend fun eliminar(id: String, userId: String)

    @Query("SELECT * FROM actividades WHERE sincronizado = 0 AND userId = :userId")
    suspend fun obtenerNoSincronizadas(userId: String): List<ActividadEntity>

    @Query("UPDATE actividades SET sincronizado = 1 WHERE id = :id")
    suspend fun marcarSincronizada(id: String)
}