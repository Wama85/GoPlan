package com.softwama.goplan.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.softwama.goplan.features.proyectos.domain.model.Proyecto
@Entity(tableName = "proyectos")
data class ProyectoEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val nombre: String,
    val descripcion: String,
    val colorHex: String,
    val progreso: Float,
    val fechaCreacion: Long,
    val fechaInicio: Long,
    val fechaFin: Long,
    val sincronizado: Boolean = false
) {
    fun toDomain(): Proyecto = Proyecto(
        id = id,
        nombre = nombre,
        descripcion = descripcion,
        colorHex = colorHex,
        progreso = progreso,
        fechaCreacion = fechaCreacion,
        fechaInicio = fechaInicio,
        fechaFin = fechaFin
    )

    companion object {
        fun fromDomain(proyecto: Proyecto, userId: String, sincronizado: Boolean = false): ProyectoEntity = ProyectoEntity(
            id = proyecto.id,
            userId = userId,
            nombre = proyecto.nombre,
            descripcion = proyecto.descripcion,
            colorHex = proyecto.colorHex,
            progreso = proyecto.progreso,
            fechaCreacion = proyecto.fechaCreacion,
            fechaInicio = proyecto.fechaInicio,
            fechaFin = proyecto.fechaFin,
            sincronizado = sincronizado
        )
    }
}