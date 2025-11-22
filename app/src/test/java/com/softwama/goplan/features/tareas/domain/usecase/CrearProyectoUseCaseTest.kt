package com.softwama.goplan.features.proyectos.domain.usecase

import com.softwama.goplan.features.proyectos.domain.model.Proyecto
import com.softwama.goplan.features.proyectos.domain.repository.ProyectoRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class CrearProyectoUseCaseTest {

    private lateinit var proyectoRepository: ProyectoRepository
    private lateinit var crearProyectoUseCase: CrearProyectoUseCase

    @Before
    fun setup() {
        proyectoRepository = mockk(relaxed = true)
        crearProyectoUseCase = CrearProyectoUseCase(proyectoRepository)
    }

    @Test
    fun `al crear proyecto debe llamar al repositorio`() = runTest {
        val proyecto = Proyecto(
            id = "",
            nombre = "Mi proyecto de prueba",
            descripcion = "Descripción del proyecto",
            colorHex = "#2196F3",
            progreso = 0f,
            fechaCreacion = System.currentTimeMillis(),
            fechaInicio = System.currentTimeMillis(),
            fechaFin = System.currentTimeMillis() + 86400000
        )

        crearProyectoUseCase(proyecto)

        coVerify { proyectoRepository.crearProyecto(proyecto) }
    }

    @Test
    fun `proyecto nuevo debe tener progreso en cero`() {
        val proyecto = Proyecto(
            id = "1",
            nombre = "Nuevo proyecto",
            descripcion = "",
            fechaInicio = System.currentTimeMillis(),
            fechaFin = System.currentTimeMillis()
        )

        assert(proyecto.progreso == 0f)
    }

    @Test
    fun `proyecto nuevo debe tener color por defecto`() {
        val proyecto = Proyecto(
            id = "1",
            nombre = "Proyecto",
            descripcion = "",
            fechaInicio = System.currentTimeMillis(),
            fechaFin = System.currentTimeMillis()
        )

        assert(proyecto.colorHex == "#2196F3")
    }
}