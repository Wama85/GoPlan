package com.softwama.goplan.features.estadisticas.data.repository

import com.softwama.goplan.features.estadisticas.domain.model.Estadistica
import com.softwama.goplan.features.estadisticas.domain.repository.EstadisticaRepository
import com.softwama.goplan.features.tareas.domain.repository.TareaRepository
import com.softwama.goplan.features.proyectos.domain.repository.ProyectoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.util.Calendar

class EstadisticaRepositoryImpl(
    private val tareaRepository: TareaRepository,
    private val proyectoRepository: ProyectoRepository
) : EstadisticaRepository {

    override fun obtenerEstadisticas(): Flow<Estadistica> {
        return combine(
            tareaRepository.obtenerTareas(),
            proyectoRepository.obtenerProyectos()
        ) { tareas, proyectos ->

            val ahora = System.currentTimeMillis()
            val calendar = Calendar.getInstance()

            // Inicio del día actual
            calendar.apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val inicioDia = calendar.timeInMillis

            // Inicio de la semana
            calendar.apply {
                set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
            }
            val inicioSemana = calendar.timeInMillis

            // Inicio del mes
            calendar.apply {
                set(Calendar.DAY_OF_MONTH, 1)
            }
            val inicioMes = calendar.timeInMillis

            val tareasCompletadas = tareas.filter { it.completada }
            val tareasHoy = tareasCompletadas.count { it.fechaCreacion >= inicioDia }
            val tareasSemana = tareasCompletadas.count { it.fechaCreacion >= inicioSemana }
            val tareasMes = tareasCompletadas.count { it.fechaCreacion >= inicioMes }

            val productividad = if (tareas.isNotEmpty()) {
                tareasCompletadas.size.toFloat() / tareas.size.toFloat()
            } else 0f

            Estadistica(
                totalTareas = tareas.size,
                totalProyectos = proyectos.size,
                productividad = productividad,
                tareasHoy = tareasHoy,
                tareasSemana = tareasSemana,
                tareasMes = tareasMes,
                tiempoPromedio = 30 // Valor fijo por ahora
            )
        }
    }
}