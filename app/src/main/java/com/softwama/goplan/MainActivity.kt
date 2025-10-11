package com.softwama.goplan

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.softwama.goplan.navigation.AppNavigation
import com.softwama.goplan.navigation.NavigationDrawer
import io.sentry.Sentry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MainActivity : ComponentActivity() {

    // Launcher para pedir permisos de notificaciones
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.d(TAG, "✅ Permiso de notificaciones concedido")
            getFirebaseToken()
        } else {
            Log.w(TAG, "⚠️ Permiso de notificaciones denegado")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar Firebase
        FirebaseApp.initializeApp(this)

        // Sentry - waiting for view to draw
        findViewById<android.view.View>(android.R.id.content).viewTreeObserver.addOnGlobalLayoutListener {
            try {
                throw Exception("This app uses Sentry! :)")
            } catch (e: Exception) {
                Sentry.captureException(e)
            }
        }

        // Pedir permisos de notificaciones
        askNotificationPermission()

        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                MainApp()
            }
        }
    }

    /**
     * Pide permiso de notificaciones en Android 13+ (API 33+)
     */
    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    Log.d(TAG, "✅ Ya tiene permiso de notificaciones")
                    getFirebaseToken()
                }
                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    // Aquí puedes mostrar un diálogo explicando por qué necesitas el permiso
                    Log.d(TAG, "ℹ️ Mostrando rationale para permisos")
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
                else -> {
                    // Solicitar permiso directamente
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            // Android 12 o menor, no necesita permiso explícito
            Log.d(TAG, "📱 Android < 13, no se requiere permiso POST_NOTIFICATIONS")
            getFirebaseToken()
        }
    }

    /**
     * Obtiene el token FCM del dispositivo
     */
    private fun getFirebaseToken() {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.e(TAG, "❌ Error obteniendo token FCM", task.exception)
                    return@addOnCompleteListener
                }

                // Token FCM obtenido exitosamente
                val token = task.result
                Log.d(TAG, "🔑 Token FCM: $token")

                // TODO: Envía este token a tu backend
                sendTokenToBackend(token)

                // Opcional: Suscribirse a temas
                subscribeToTopics()
            }
    }

    /**
     * Envía el token a tu backend (implementar según tu API)
     */
    private fun sendTokenToBackend(token: String) {
        // TODO: Implementar llamada a tu API
        Log.d(TAG, "📤 Enviando token al backend: $token")

        // Ejemplo con Retrofit (implementar según tu caso):
        // lifecycleScope.launch {
        //     try {
        //         apiService.updateFcmToken(token)
        //         Log.d(TAG, "✅ Token enviado al backend exitosamente")
        //     } catch (e: Exception) {
        //         Log.e(TAG, "❌ Error enviando token al backend", e)
        //     }
        // }
    }

    /**
     * Suscribe el dispositivo a temas de notificaciones
     */
    private fun subscribeToTopics() {
        // Suscribirse a un tema general
        FirebaseMessaging.getInstance().subscribeToTopic("all_users")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "✅ Suscrito al tema: all_users")
                } else {
                    Log.e(TAG, "❌ Error suscribiéndose al tema", task.exception)
                }
            }

        // Puedes suscribirte a más temas según tu lógica de negocio
        // FirebaseMessaging.getInstance().subscribeToTopic("premium_users")
        // FirebaseMessaging.getInstance().subscribeToTopic("weekly_reminders")
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationDrawerHost(
    coroutineScope: CoroutineScope,
    drawerState: DrawerState,
    navController: NavHostController
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                modifier = Modifier.statusBarsPadding(),
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                ),
                navigationIcon = {
                    IconButton(onClick = {
                        coroutineScope.launch {
                            drawerState.open()
                        }
                    }) {
                        Icon(
                            Icons.Default.Menu,
                            contentDescription = "Menu"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        AppNavigation(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp() {
    val navController: NavHostController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentRoute = currentDestination?.route

    val navigationDrawerItems = listOf(
        NavigationDrawer.Dashboard
        //Añadir otra ventanas
    )

    val drawerState = rememberDrawerState(
        initialValue = DrawerValue.Closed
    )
    val coroutineScope = rememberCoroutineScope()

    // Rutas que NO deben mostrar el drawer (Login y Suscribe)
    val routesWithoutDrawer = listOf("login", "suscribe")
    val showDrawer = currentRoute !in routesWithoutDrawer

    if (showDrawer) {
        // Mostrar CON drawer (Dashboard, Calendar, Profile)
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(
                    modifier = Modifier.width(280.dp)
                ) {
                    // Header del drawer con logo
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.icono),
                                contentDescription = "Logo",
                                modifier = Modifier.size(80.dp)
                            )
                        }
                    }

                    HorizontalDivider()

                    Spacer(modifier = Modifier.height(16.dp))

                    // Items del menú
                    navigationDrawerItems.forEach { item ->
                        val isSelected = currentDestination?.route == item.route

                        NavigationDrawerItem(
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.label
                                )
                            },
                            label = { Text(item.label) },
                            selected = isSelected,
                            onClick = {
                                navController.navigate(item.route) {
                                    launchSingleTop = true
                                    restoreState = true
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                }
                                coroutineScope.launch {
                                    drawerState.close()
                                }
                            },
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                    }
                }
            }
        ) {
            NavigationDrawerHost(coroutineScope, drawerState, navController)
        }
    } else {
        // Mostrar SIN drawer (Login, Suscribe)
        AppNavigation(
            navController = navController,
            modifier = Modifier.fillMaxSize()
        )
    }
}