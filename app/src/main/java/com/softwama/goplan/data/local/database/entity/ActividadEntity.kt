package com.softwama.goplan.data.local.database.entity


import androidx.room.Entity
import androidx.room.PrimaryKey
import com.softwama.goplan.features.proyectos.domain.model.Actividad

@Entity(tableName = "actividades")
data class ActividadEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val proyectoId: String,
    val nombre: String,
    val descripcion: String,
    val completada: Boolean,
    val fechaInicio: Long,
    val fechaFin: Long,
    val sincronizado: Boolean = false
) {
    fun toDomain(): Actividad = Actividad(
        id = id,
        proyectoId = proyectoId,
        nombre = nombre,
        descripcion = descripcion,
        completada = completada,
        fechaInicio = fechaInicio,
        fechaFin = fechaFin
    )

    companion object {
        fun fromDomain(actividad: Actividad, userId: String, sincronizado: Boolean = false): ActividadEntity = ActividadEntity(
            id = actividad.id,
            userId = userId,
            proyectoId = actividad.proyectoId,
            nombre = actividad.nombre,
            descripcion = actividad.descripcion,
            completada = actividad.completada,
            fechaInicio = actividad.fechaInicio,
            fechaFin = actividad.fechaFin,
            sincronizado = sincronizado
        )
    }
}