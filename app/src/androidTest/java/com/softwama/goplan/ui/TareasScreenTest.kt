package com.softwama.goplan.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.softwama.goplan.features.tareas.domain.model.Tarea
import com.softwama.goplan.features.tareas.presentation.TareaItem
import com.softwama.goplan.ui.theme.GoPlanTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TareasScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun tareaItem_muestra_titulo_correctamente() {
        val tarea = Tarea(
            id = "1",
            titulo = "Tarea de prueba",
            descripcion = "Descripción de prueba",
            completada = false,
            fechaCreacion = System.currentTimeMillis()
        )

        composeTestRule.setContent {
            GoPlanTheme {
                TareaItem(
                    tarea = tarea,
                    onCompletarClick = {},
                    onEliminarClick = {},
                   onEditarClick = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Tarea de prueba").assertIsDisplayed()
        composeTestRule.onNodeWithText("Descripción de prueba").assertIsDisplayed()
    }

    @Test
    fun tareaItem_muestra_tarea_completada() {
        val tarea = Tarea(
            id = "2",
            titulo = "Tarea completada",
            descripcion = "",
            completada = true,
            fechaCreacion = System.currentTimeMillis()
        )

        composeTestRule.setContent {
            GoPlanTheme {
                TareaItem(
                    tarea = tarea,
                    onCompletarClick = {},
                    onEliminarClick = {},
                     onEditarClick = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Tarea completada").assertIsDisplayed()
    }

    @Test
    fun tareaItem_muestra_fecha_vencimiento() {
        val tarea = Tarea(
            id = "3",
            titulo = "Tarea con fecha",
            descripcion = "",
            completada = false,
            fechaCreacion = System.currentTimeMillis(),
            fechaVencimiento = System.currentTimeMillis() + 86400000
        )

        composeTestRule.setContent {
            GoPlanTheme {
                TareaItem(
                    tarea = tarea,
                    onCompletarClick = {},
                    onEliminarClick = {},
                    onEditarClick = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Tarea con fecha").assertIsDisplayed()
    }
}