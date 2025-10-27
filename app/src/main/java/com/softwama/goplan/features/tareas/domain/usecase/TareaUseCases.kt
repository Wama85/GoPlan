
package com.softwama.goplan.features.tareas.domain.usecase

data class TareaUseCases(
    val obtenerTareasUseCase: ObtenerTareasUseCase,
    val crearTareaUseCase: CrearTareaUseCase,
    val actualizarTareaUseCase: ActualizarTareaUseCase,
    val eliminarTareaUseCase: EliminarTareaUseCase
)