package com.softwama.goplan.features.tareas.domain.model

data class Tarea(
    val id: String = "",
    val titulo: String,
    val descripcion: String = "",
    val completada: Boolean = false,
    val proyectoId: String = "",
    val fechaCreacion: Long = System.currentTimeMillis()
)