package com.softwama.goplan.features.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.softwama.goplan.navigation.Screen
import com.softwama.goplan.ui.theme.ButtonOrange
import com.softwama.goplan.R
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavController,
    viewModel: DashboardViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        stringResource(id = R.string.dashboard_title),
                        fontWeight = FontWeight.Bold
                    )
                },
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Saludo al usuario
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = ButtonOrange.copy(alpha = 0.1f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        Icons.Default.Home,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = ButtonOrange
                    )
                    Column {
                        Text(
                            text = stringResource(id = R.string.dashboard_welcome),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = state.userName,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            // Sección de acciones rápidas
            Text(
                text = stringResource(id = R.string.dashboard_quick_actions),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            // Tarjetas de opciones
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DashboardCard(
                    title = stringResource(id = R.string.dashboard_tasks),
                    icon = Icons.Default.Task,
                    color = Color(0xFF4CAF50),
                    modifier = Modifier.weight(1f),
                    onClick = {
                        navController.navigate(Screen.Tareas.route)
                    }
                )

                DashboardCard(
                    title = stringResource(id = R.string.dashboard_projects),
                    icon = Icons.Default.Folder,
                    color = Color(0xFF2196F3),
                    modifier = Modifier.weight(1f),
                    onClick = {
                        navController.navigate(Screen.Proyectos.route)
                    }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DashboardCard(
                    title = stringResource(id = R.string.dashboard_calendar),
                    icon = Icons.Default.CalendarToday,
                    color = Color(0xFFFF9800),
                    modifier = Modifier.weight(1f),
                    onClick = {
                        navController.navigate(Screen.Calendar.route)
                    }
                )

                DashboardCard(
                    title = stringResource(id = R.string.dashboard_stats),
                    icon = Icons.Default.BarChart,
                    color = Color(0xFF9C27B0),
                    modifier = Modifier.weight(1f),
                    onClick = {
                        navController.navigate(Screen.Estadisticas.route)
                    }
                )
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun DashboardCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier,
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = color
            )
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}
