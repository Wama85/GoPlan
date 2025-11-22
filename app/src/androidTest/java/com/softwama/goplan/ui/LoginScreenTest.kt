package com.softwama.goplan.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.softwama.goplan.features.login.presentation.LoginScreen
import com.softwama.goplan.ui.theme.GoPlanTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun loginScreen_muestra_boton_iniciar_sesion() {
        composeTestRule.setContent {
            GoPlanTheme {
                LoginScreen(
                    onLoginSuccess = {},
                    onNavigateToSuscribe = {}
                )
            }
        }

        composeTestRule.onAllNodesWithText("Iniciar Sesión")[0].assertIsDisplayed()
    }

    @Test
    fun loginScreen_muestra_campos_de_texto() {
        composeTestRule.setContent {
            GoPlanTheme {
                LoginScreen(
                    onLoginSuccess = {},
                    onNavigateToSuscribe = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Correo electrónico").assertIsDisplayed()
        composeTestRule.onNodeWithText("Contraseña").assertIsDisplayed()
    }
}