
package com.softwama.goplan.features.proyectos.domain.model

data class Proyecto(
    val id: String = "",
    val nombre: String,
    val descripcion: String = "",
    val colorHex: String = "#2196F3",
    val progreso: Float = 0f,
    val fechaCreacion: Long = System.currentTimeMillis()
)