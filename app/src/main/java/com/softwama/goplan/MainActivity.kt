package com.softwama.goplan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.softwama.goplan.navigation.AppNavigation
import com.softwama.goplan.navigation.NavigationDrawer
import io.sentry.Sentry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // waiting for view to draw to better represent a captured error with a screenshot
        findViewById<android.view.View>(android.R.id.content).viewTreeObserver.addOnGlobalLayoutListener {
            try {
                throw Exception("This app uses Sentry! :)")
            } catch (e: Exception) {
                Sentry.captureException(e)
            }
        }

        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                MainApp()
            }
        }
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
                            Text(
                                text = "GoPlan",
                                style = MaterialTheme.typography.titleLarge
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