package com.softwama.goplan.data.local.database.dao

import androidx.room.*
import com.softwama.goplan.data.local.database.entity.ProyectoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProyectoDao {
    @Query("SELECT * FROM proyectos WHERE userId = :userId ORDER BY fechaCreacion DESC")
    fun obtenerTodos(userId: String): Flow<List<ProyectoEntity>>

    @Query("SELECT * FROM proyectos WHERE id = :id AND userId = :userId")
    suspend fun obtenerPorId(id: String, userId: String): ProyectoEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(proyecto: ProyectoEntity)

    @Update
    suspend fun actualizar(proyecto: ProyectoEntity)

    @Query("DELETE FROM proyectos WHERE id = :id AND userId = :userId")
    suspend fun eliminar(id: String, userId: String)

    @Query("SELECT * FROM proyectos WHERE sincronizado = 0 AND userId = :userId")
    suspend fun obtenerNoSincronizados(userId: String): List<ProyectoEntity>

    @Query("UPDATE proyectos SET sincronizado = 1 WHERE id = :id")
    suspend fun marcarSincronizado(id: String)
}