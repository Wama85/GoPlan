package com.softwama.goplan.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.core.app.ApplicationProvider // Importación necesaria para obtener el contexto
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.softwama.goplan.features.login.presentation.LoginScreen
import com.softwama.goplan.ui.theme.GoPlanTheme
import com.softwama.goplan.R // Importación para recursos de strings
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // 1. Obtener el contexto de la aplicación (soluciona el error 'activity')
    private val context = ApplicationProvider.getApplicationContext<android.content.Context>()

    @Test
    fun loginScreen_muestra_boton_iniciar_sesion() {
        composeTestRule.setContent {
            GoPlanTheme {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    LoginScreen(
                        onLoginSuccess = {},
                        onNavigateToSuscribe = {}
                    )
                }
            }
        }

        // 2. Usar el objeto 'context' para obtener el string
        val loginButtonText = context.getString(R.string.btn_iniciar_sesion)
        composeTestRule.onNodeWithText(loginButtonText).assertIsDisplayed()
    }

    @Test
    fun loginScreen_muestra_campos_de_texto() {
        composeTestRule.setContent {
            GoPlanTheme {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    LoginScreen(
                        onLoginSuccess = {},
                        onNavigateToSuscribe = {}
                    )
                }
            }
        }

        // 3. Usar el objeto 'context' para obtener el string de email
        val emailLabel = context.getString(R.string.correo_electronico)
        composeTestRule.onNodeWithText(emailLabel).assertIsDisplayed()

        // 4. Usar el objeto 'context' para obtener el string de contraseña
        val passwordLabel = context.getString(R.string.contrasena)
        composeTestRule.onNodeWithText(passwordLabel).assertIsDisplayed()
    }
}