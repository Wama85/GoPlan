
package com.softwama.goplan.features.proyectos.domain.model

data class Actividad(
    val id: String = "",
    val proyectoId: String,
    val nombre: String,
    val descripcion: String = "",
    val completada: Boolean = false,
    val fechaInicio: Long,
    val fechaFin: Long
)