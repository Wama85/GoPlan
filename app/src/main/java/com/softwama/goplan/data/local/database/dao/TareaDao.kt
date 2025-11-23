package com.softwama.goplan.data.local.database.dao

import androidx.room.*
import com.softwama.goplan.data.local.database.entity.TareaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TareaDao {
    @Query("SELECT * FROM tareas WHERE userId = :userId ORDER BY fechaCreacion DESC")
    fun obtenerTodas(userId: String): Flow<List<TareaEntity>>

    @Query("SELECT * FROM tareas WHERE id = :id AND userId = :userId")
    suspend fun obtenerPorId(id: String, userId: String): TareaEntity?

    @Query("SELECT * FROM tareas WHERE proyectoId = :proyectoId AND userId = :userId")
    fun obtenerPorProyecto(proyectoId: String, userId: String): Flow<List<TareaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(tarea: TareaEntity)

    @Update
    suspend fun actualizar(tarea: TareaEntity)

    @Query("DELETE FROM tareas WHERE id = :id AND userId = :userId")
    suspend fun eliminar(id: String, userId: String)

    @Query("SELECT * FROM tareas WHERE sincronizado = 0 AND userId = :userId")
    suspend fun obtenerNoSincronizadas(userId: String): List<TareaEntity>

    @Query("UPDATE tareas SET sincronizado = 1 WHERE id = :id")
    suspend fun marcarSincronizada(id: String)
}