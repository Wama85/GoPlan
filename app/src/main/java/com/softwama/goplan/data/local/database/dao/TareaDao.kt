package com.softwama.goplan.data.local.database.dao

import androidx.room.*
import com.softwama.goplan.data.local.database.entity.TareaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TareaDao {
    @Query("SELECT * FROM tareas ORDER BY fechaCreacion DESC")
    fun obtenerTodas(): Flow<List<TareaEntity>>

    @Query("SELECT * FROM tareas WHERE id = :id")
    suspend fun obtenerPorId(id: String): TareaEntity?

    @Query("SELECT * FROM tareas WHERE proyectoId = :proyectoId")
    fun obtenerPorProyecto(proyectoId: String): Flow<List<TareaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(tarea: TareaEntity)

    @Update
    suspend fun actualizar(tarea: TareaEntity)

    @Query("DELETE FROM tareas WHERE id = :id")
    suspend fun eliminar(id: String)

    @Query("SELECT * FROM tareas WHERE sincronizado = 0")
    suspend fun obtenerNoSincronizadas(): List<TareaEntity>

    @Query("UPDATE tareas SET sincronizado = 1 WHERE id = :id")
    suspend fun marcarSincronizada(id: String)
}