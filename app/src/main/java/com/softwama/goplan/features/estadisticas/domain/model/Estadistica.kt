package com.softwama.goplan.features.estadisticas.domain.model

data class Estadistica(
    val totalTareas: Int = 0,
    val totalProyectos: Int = 0,
    val productividad: Float = 0f,
    val tareasHoy: Int = 0,
    val tareasSemana: Int = 0,
    val tareasMes: Int = 0,
    val tiempoPromedio: Int = 0
)