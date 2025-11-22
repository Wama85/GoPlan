package com.softwama.goplan

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.ViewTreeObserver
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
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.softwama.goplan.data.local.datastore.UserPreferencesDataStore
import com.softwama.goplan.navigation.AppNavigation
import com.softwama.goplan.navigation.NavigationDrawer
import com.softwama.goplan.ui.theme.GoPlanTheme
import io.sentry.Sentry
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.compose.KoinAndroidContext
import org.koin.compose.koinInject

class MainActivity : ComponentActivity() {

    private val dataStore: UserPreferencesDataStore by inject()

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

    private var globalLayoutListener: ViewTreeObserver.OnGlobalLayoutListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)

        val contentView = findViewById<android.view.View>(android.R.id.content)
        globalLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
            contentView.viewTreeObserver.removeOnGlobalLayoutListener(globalLayoutListener)
            try {
                throw Exception("This app uses Sentry! :)")
            } catch (e: Exception) {
                Sentry.captureException(e)
            }
        }
        contentView.viewTreeObserver.addOnGlobalLayoutListener(globalLayoutListener)

        askNotificationPermission()

        setContent {
            KoinAndroidContext {
                val dataStore: UserPreferencesDataStore = koinInject()
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

    override fun onDestroy() {
        super.onDestroy()
        globalLayoutListener?.let {
            findViewById<android.view.View>(android.R.id.content)
                ?.viewTreeObserver
                ?.removeOnGlobalLayoutListener(it)
        }
        Log.d(TAG, "🧹 MainActivity destruida")
    }

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
                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            Log.d(TAG, "📱 Android < 13, no se requiere permiso POST_NOTIFICATIONS")
            getFirebaseToken()
        }
    }

    private fun getFirebaseToken() {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.e(TAG, "❌ Error obteniendo token FCM", task.exception)
                    return@addOnCompleteListener
                }

                val token = task.result
                Log.d(TAG, "🔑 Token FCM: $token")

                lifecycleScope.launch {
                    dataStore.saveFcmToken(token)
                }

                subscribeToTopics()
            }
    }

    private fun subscribeToTopics() {
        FirebaseMessaging.getInstance().subscribeToTopic("all_users")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "✅ Suscrito al tema: all_users")
                } else {
                    Log.e(TAG, "❌ Error suscribiéndose al tema", task.exception)
                }
            }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp() {
    val navController: NavHostController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentRoute = currentDestination?.route
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

    val routesWithoutDrawer = listOf("login", "suscribe", "tareas", "proyectos", "estadisticas","calendar","profile","notifications","editProfile","settingsScreen")
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
                                    coroutineScope.launch {
                                        drawerState.close()
                                    }
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
    } else {
        AppNavigation(
            navController = navController,
            modifier = Modifier.fillMaxSize()
        )
    }
}