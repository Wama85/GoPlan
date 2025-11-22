package com.softwama.goplan.data.local.database.dao

import androidx.room.*
import com.softwama.goplan.data.local.database.entity.ProyectoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProyectoDao {
    @Query("SELECT * FROM proyectos ORDER BY fechaCreacion DESC")
    fun obtenerTodos(): Flow<List<ProyectoEntity>>

    @Query("SELECT * FROM proyectos WHERE id = :id")
    suspend fun obtenerPorId(id: String): ProyectoEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(proyecto: ProyectoEntity)

    @Update
    suspend fun actualizar(proyecto: ProyectoEntity)

    @Query("DELETE FROM proyectos WHERE id = :id")
    suspend fun eliminar(id: String)

    @Query("SELECT * FROM proyectos WHERE sincronizado = 0")
    suspend fun obtenerNoSincronizados(): List<ProyectoEntity>

    @Query("UPDATE proyectos SET sincronizado = 1 WHERE id = :id")
    suspend fun marcarSincronizado(id: String)
}