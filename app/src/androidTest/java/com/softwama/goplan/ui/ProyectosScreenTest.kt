package com.softwama.goplan.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.softwama.goplan.features.proyectos.domain.model.Proyecto
import com.softwama.goplan.features.proyectos.presentation.ProyectoCard
import com.softwama.goplan.ui.theme.GoPlanTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProyectosScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun proyectoCard_muestra_nombre_correctamente() {
        val proyecto = Proyecto(
            id = "1",
            nombre = "Proyecto de prueba",
            descripcion = "Descripción del proyecto",
            colorHex = "#2196F3",
            progreso = 0.5f,
            fechaCreacion = System.currentTimeMillis(),
            fechaInicio = System.currentTimeMillis(),
            fechaFin = System.currentTimeMillis() + 86400000
        )

        composeTestRule.setContent {
            GoPlanTheme {
                ProyectoCard(
                    proyecto = proyecto,
                    onClick = {},
                    onEditClick = {},
                    onDeleteClick = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Proyecto de prueba").assertIsDisplayed()
        composeTestRule.onNodeWithText("Descripción del proyecto").assertIsDisplayed()
    }

    @Test
    fun proyectoCard_muestra_progreso_correctamente() {
        val proyecto = Proyecto(
            id = "2",
            nombre = "Proyecto con progreso",
            descripcion = "",
            colorHex = "#4CAF50",
            progreso = 0.75f,
            fechaCreacion = System.currentTimeMillis(),
            fechaInicio = System.currentTimeMillis(),
            fechaFin = System.currentTimeMillis() + 86400000
        )

        composeTestRule.setContent {
            GoPlanTheme {
                ProyectoCard(
                    proyecto = proyecto,
                    onClick = {},
                    onEditClick = {},
                    onDeleteClick = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Proyecto con progreso").assertIsDisplayed()
        composeTestRule.onNodeWithText("75% completado").assertIsDisplayed()
    }

    @Test
    fun proyectoCard_muestra_proyecto_sin_descripcion() {
        val proyecto = Proyecto(
            id = "3",
            nombre = "Proyecto sin descripción",
            descripcion = "",
            colorHex = "#FF9800",
            progreso = 0f,
            fechaCreacion = System.currentTimeMillis(),
            fechaInicio = System.currentTimeMillis(),
            fechaFin = System.currentTimeMillis() + 86400000
        )

        composeTestRule.setContent {
            GoPlanTheme {
                ProyectoCard(
                    proyecto = proyecto,
                    onClick = {},
                    onEditClick = {},
                    onDeleteClick = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Proyecto sin descripción").assertIsDisplayed()
        composeTestRule.onNodeWithText("0% completado").assertIsDisplayed()
    }

    @Test
    fun proyectoCard_muestra_proyecto_completado() {
        val proyecto = Proyecto(
            id = "4",
            nombre = "Proyecto completado",
            descripcion = "Este proyecto está terminado",
            colorHex = "#9C27B0",
            progreso = 1.0f,
            fechaCreacion = System.currentTimeMillis(),
            fechaInicio = System.currentTimeMillis() - 86400000,
            fechaFin = System.currentTimeMillis()
        )

        composeTestRule.setContent {
            GoPlanTheme {
                ProyectoCard(
                    proyecto = proyecto,
                    onClick = {},
                    onEditClick = {},
                    onDeleteClick = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Proyecto completado").assertIsDisplayed()
        composeTestRule.onNodeWithText("100% completado").assertIsDisplayed()
    }
}