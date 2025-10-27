
package com.softwama.goplan.features.proyectos.domain.usecase

data class ProyectoUseCases(
    val obtenerProyectosUseCase: ObtenerProyectosUseCase,
    val crearProyectoUseCase: CrearProyectoUseCase,
    val actualizarProyectoUseCase: ActualizarProyectoUseCase,
    val eliminarProyectoUseCase: EliminarProyectoUseCase
)