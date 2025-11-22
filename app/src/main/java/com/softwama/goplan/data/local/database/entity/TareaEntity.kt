package com.softwama.goplan.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.softwama.goplan.features.tareas.domain.model.Tarea

@Entity(tableName = "tareas")
data class TareaEntity(
    @PrimaryKey
    val id: String,
    val titulo: String,
    val descripcion: String,
    val completada: Boolean,
    val proyectoId: String,
    val fechaCreacion: Long,
    val fechaVencimiento: Long?,
    val sincronizado: Boolean = false
) {
    fun toDomain(): Tarea = Tarea(
        id = id,
        titulo = titulo,
        descripcion = descripcion,
        completada = completada,
        proyectoId = proyectoId,
        fechaCreacion = fechaCreacion,
        fechaVencimiento = fechaVencimiento
    )

    companion object {
        fun fromDomain(tarea: Tarea, sincronizado: Boolean = false): TareaEntity = TareaEntity(
            id = tarea.id,
            titulo = tarea.titulo,
            descripcion = tarea.descripcion,
            completada = tarea.completada,
            proyectoId = tarea.proyectoId,
            fechaCreacion = tarea.fechaCreacion,
            fechaVencimiento = tarea.fechaVencimiento,
            sincronizado = sincronizado
        )
    }
}