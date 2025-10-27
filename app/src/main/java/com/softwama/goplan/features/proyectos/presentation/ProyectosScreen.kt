
package com.softwama.goplan.features.proyectos.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.softwama.goplan.features.proyectos.domain.model.Proyecto
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProyectosScreen(
    navController: NavController,
    viewModel: ProyectosViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Proyectos",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nuevo Proyecto")
            }
        }
    ) { padding ->
        if (state.proyectos.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.Folder,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                    )
                    Text(
                        "No hay proyectos",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.proyectos) { proyecto ->
                    ProyectoCard(
                        proyecto = proyecto,
                        onEliminarClick = { viewModel.eliminarProyecto(proyecto.id) }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        NuevoProyectoDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { nombre, descripcion, color ->
                viewModel.agregarProyecto(nombre, descripcion, color)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun ProyectoCard(
    proyecto: Proyecto,
    onEliminarClick: () -> Unit
) {
    val color = try {
        Color(android.graphics.Color.parseColor(proyecto.colorHex))
    } catch (e: Exception) {
        Color(0xFF2196F3)
    }

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        modifier = Modifier.size(40.dp),
                        shape = MaterialTheme.shapes.medium,
                        color = color.copy(alpha = 0.2f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.Folder,
                                contentDescription = null,
                                tint = color
                            )
                        }
                    }

                    Text(
                        text = proyecto.nombre,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(onClick = onEliminarClick) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            if (proyecto.descripcion.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = proyecto.descripcion,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = { proyecto.progreso },
                modifier = Modifier.fillMaxWidth(),
                color = color
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${(proyecto.progreso * 100).toInt()}% completado",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
fun NuevoProyectoDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, Color) -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var colorSeleccionado by remember { mutableStateOf(Color(0xFF2196F3)) }

    val coloresDisponibles = listOf(
        Color(0xFF2196F3),
        Color(0xFF4CAF50),
        Color(0xFFFF9800),
        Color(0xFF9C27B0),
        Color(0xFFF44336),
        Color(0xFF00BCD4)
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuevo Proyecto") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción (opcional)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )

                Text(
                    "Selecciona un color:",
                    style = MaterialTheme.typography.bodyMedium
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    coloresDisponibles.forEach { color ->
                        Surface(
                            modifier = Modifier.size(40.dp),
                            shape = MaterialTheme.shapes.medium,
                            color = color,
                            border = if (color == colorSeleccionado) {
                                androidx.compose.foundation.BorderStroke(
                                    3.dp,
                                    MaterialTheme.colorScheme.primary
                                )
                            } else null,
                            onClick = { colorSeleccionado = color }
                        ) {}
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(nombre, descripcion, colorSeleccionado) },
                enabled = nombre.isNotBlank()
            ) {
                Text("Crear")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}