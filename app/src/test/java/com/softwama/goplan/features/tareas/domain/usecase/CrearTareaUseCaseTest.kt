package com.softwama.goplan.features.tareas.domain.usecase

import com.softwama.goplan.features.tareas.domain.model.Tarea
import com.softwama.goplan.features.tareas.domain.repository.TareaRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class CrearTareaUseCaseTest {

    private lateinit var tareaRepository: TareaRepository
    private lateinit var crearTareaUseCase: CrearTareaUseCase

    @Before
    fun setup() {
        tareaRepository = mockk(relaxed = true)
        crearTareaUseCase = CrearTareaUseCase(tareaRepository)
    }

    @Test
    fun `al crear tarea debe llamar al repositorio`() = runTest {
        val tarea = Tarea(
            id = "",
            titulo = "Mi tarea de prueba",
            descripcion = "Descripción",
            completada = false,
            fechaCreacion = System.currentTimeMillis()
        )

        crearTareaUseCase(tarea)

        coVerify { tareaRepository.crearTarea(tarea) }
    }

    @Test
    fun `tarea nueva no debe estar completada`() {
        val tarea = Tarea(
            id = "1",
            titulo = "Nueva tarea",
            descripcion = ""
        )

        assert(!tarea.completada)
    }
}