
package com.softwama.goplan.features.proyectos.domain.usecase

data class ActividadUseCases(
    val obtenerActividadesUseCase: ObtenerActividadesUseCase,
    val crearActividadUseCase: CrearActividadUseCase,
    val actualizarActividadUseCase: ActualizarActividadUseCase,
    val eliminarActividadUseCase: EliminarActividadUseCase
)