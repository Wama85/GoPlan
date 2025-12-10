package com.softwama.goplan

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.softwama.goplan.data.local.datastore.UserPreferencesDataStore
import com.softwama.goplan.features.maintenance.presentation.MaintenanceScreen
import com.softwama.goplan.features.maintenance.presentation.MaintenanceViewModel
import com.softwama.goplan.navigation.AppNavigation
import com.softwama.goplan.navigation.NavigationDrawer
import com.softwama.goplan.ui.theme.GoPlanTheme
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.compose.KoinAndroidContext
import org.koin.compose.koinInject
import java.util.Locale

class MainActivity : ComponentActivity() {

    private val dataStore: UserPreferencesDataStore by inject()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) getFirebaseToken()
        else Log.w(TAG, "⚠️ Permiso de notificaciones denegado")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)

        askNotificationPermission()

        setContent {
            KoinAndroidContext {

                val language by dataStore.getLanguage().collectAsState(initial = "es")

                val currentConfiguration = LocalConfiguration.current
                
                val (localizedContext, localizedConfig) = remember(language, currentConfiguration) {
                    val locale = Locale(language)
                    Locale.setDefault(locale)
                    
                    val config = Configuration(currentConfiguration)
                    config.setLocale(locale)
                    config.setLayoutDirection(locale)
                    
                    val context = this@MainActivity.createConfigurationContext(config)
                    context to config
                }

                CompositionLocalProvider(
                    LocalConfiguration provides localizedConfig,
                    LocalContext provides localizedContext,
                    LocalActivityResultRegistryOwner provides this@MainActivity
                ) {

                    val isDarkMode by dataStore.isDarkMode().collectAsState(initial = false)

                    GoPlanTheme(darkTheme = isDarkMode) {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            MainApp()
                        }
                    }
                }
            }
        }
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED ->
                    getFirebaseToken()

                else -> requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else getFirebaseToken()
    }

    private fun getFirebaseToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) return@addOnCompleteListener

            val token = task.result
            lifecycleScope.launch { dataStore.saveFcmToken(token) }
            subscribeToTopics()
        }
    }

    private fun subscribeToTopics() {
        FirebaseMessaging.getInstance().subscribeToTopic("all_users")
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp() {
    // Inyectamos el ViewModel global de mantenimiento
    val maintenanceViewModel: MaintenanceViewModel = koinInject()
    val isMaintenance by maintenanceViewModel.isMaintenance.collectAsState()

    // Lanzamos el chequeo y polling una sola vez
    LaunchedEffect(Unit) {
        maintenanceViewModel.checkAppStatus()
        maintenanceViewModel.startPolling()
    }

    // Si está en modo mantenimiento mostramos la pantalla y salimos
    if (isMaintenance == true) {
        MaintenanceScreen()
        return
    }
    val navController: NavHostController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val dataStore: UserPreferencesDataStore = koinInject()

    val navigationDrawerItems = listOf(
        NavigationDrawer.Dashboard,
        NavigationDrawer.Tareas,
        NavigationDrawer.Proyectos,
        NavigationDrawer.Calendar,
        NavigationDrawer.Estadisticas,
        NavigationDrawer.CerrarSesion
    )

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    val routesWithoutDrawer = listOf(
        "login", "suscribe", "tareas", "proyectos", "estadisticas",
        "calendar", "profile", "notifications", "editProfile", "settingsScreen"
    )

    val showDrawer = currentRoute !in routesWithoutDrawer

    if (showDrawer) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(
                    modifier = Modifier.width(280.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Image(
                                painter = painterResource(id = R.drawable.icono),
                                contentDescription = "Logo",
                                modifier = Modifier.size(80.dp)
                            )
                        }
                    }

                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(16.dp))

                    navigationDrawerItems.forEach { item ->
                        val isSelected = currentRoute == item.route

                        NavigationDrawerItem(
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = stringResource(item.label)
                                )
                            },
                            label = { Text(stringResource(item.label)) },
                            selected = isSelected,
                            onClick = {
                                if (item.route == "logout") {
                                    coroutineScope.launch {
                                        dataStore.saveLoginStatus(false)
                                        navController.navigate("login") {
                                            popUpTo(0) { inclusive = true }
                                        }
                                        drawerState.close()
                                    }
                                } else {
                                    navController.navigate(item.route) {
                                        launchSingleTop = true
                                    }
                                    coroutineScope.launch { drawerState.close() }
                                }
                            },
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                    }
                }
            }
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
                                coroutineScope.launch { drawerState.open() }
                            }) {
                                Icon(Icons.Default.Menu, contentDescription = "Menu")
                            }
                        },
                        actions = {
                            IconButton(onClick = {
                                navController.navigate("profile")
                            }) {
                                Icon(Icons.Default.Person, contentDescription = "Perfil")
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
    } else {
        AppNavigation(
            navController = navController,
            modifier = Modifier.fillMaxSize()
        )
    }
}
