package com.softwama.goplan.features.proyectos.presentation

import androidx.compose.animation.AnimatedVisibility
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
import com.softwama.goplan.features.proyectos.domain.model.Actividad
import com.softwama.goplan.features.proyectos.domain.model.Proyecto
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProyectosScreen(
    navController: NavController,
    viewModel: ProyectosViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var proyectoParaActividad by remember { mutableStateOf<Proyecto?>(null) }

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
                    ProyectoCardExpandible(
                        proyecto = proyecto,
                        actividades = state.actividadesPorProyecto[proyecto.id] ?: emptyList(),
                        expandido = state.proyectoExpandido == proyecto.id,
                        onExpandirClick = { viewModel.toggleProyectoExpandido(proyecto.id) },
                        onEliminarClick = { viewModel.eliminarProyecto(proyecto.id) },
                        onAgregarActividadClick = { proyectoParaActividad = proyecto },
                        onToggleActividadClick = { viewModel.toggleActividadCompletada(it) },
                        onEliminarActividadClick = { viewModel.eliminarActividad(it.id) }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        NuevoProyectoDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { nombre, descripcion, color, fechaInicio, fechaFin ->
                viewModel.agregarProyecto(nombre, descripcion, color, fechaInicio, fechaFin)
                showAddDialog = false
            }
        )
    }

    proyectoParaActividad?.let { proyecto ->
        NuevaActividadDialog(
            proyecto = proyecto,
            onDismiss = { proyectoParaActividad = null },
            onConfirm = { nombre, descripcion, fechaInicio, fechaFin ->
                viewModel.agregarActividad(proyecto.id, nombre, descripcion, fechaInicio, fechaFin)
                proyectoParaActividad = null
            }
        )
    }
}

@Composable
fun ProyectoCardExpandible(
    proyecto: Proyecto,
    actividades: List<Actividad>,
    expandido: Boolean,
    onExpandirClick: () -> Unit,
    onEliminarClick: () -> Unit,
    onAgregarActividadClick: () -> Unit,
    onToggleActividadClick: (Actividad) -> Unit,
    onEliminarActividadClick: (Actividad) -> Unit
) {
    val color = try {
        Color(android.graphics.Color.parseColor(proyecto.colorHex))
    } catch (e: Exception) {
        Color(0xFF2196F3)
    }

    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

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
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
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

                    Column {
                        Text(
                            text = proyecto.nombre,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${dateFormat.format(Date(proyecto.fechaInicio))} - ${dateFormat.format(Date(proyecto.fechaFin))}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }

                Row {
                    IconButton(onClick = onExpandirClick) {
                        Icon(
                            if (expandido) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = if (expandido) "Contraer" else "Expandir"
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
                text = "${(proyecto.progreso * 100).toInt()}% completado • ${actividades.count { it.completada }}/${actividades.size} actividades",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )

            AnimatedVisibility(visible = expandido) {
                Column(
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Actividades",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(onClick = onAgregarActividadClick) {
                            Icon(Icons.Default.Add, contentDescription = "Agregar actividad")
                        }
                    }

                    if (actividades.isEmpty()) {
                        Text(
                            text = "No hay actividades",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    } else {
                        actividades.forEach { actividad ->
                            ActividadItem(
                                actividad = actividad,
                                onToggleClick = { onToggleActividadClick(actividad) },
                                onEliminarClick = { onEliminarActividadClick(actividad) }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ActividadItem(
    actividad: Actividad,
    onToggleClick: () -> Unit,
    onEliminarClick: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd/MM", Locale.getDefault())

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = actividad.completada,
                onCheckedChange = { onToggleClick() }
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = actividad.nombre,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    textDecoration = if (actividad.completada) {
                        androidx.compose.ui.text.style.TextDecoration.LineThrough
                    } else null
                )
                if (actividad.descripcion.isNotEmpty()) {
                    Text(
                        text = actividad.descripcion,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.CalendarToday,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Text(
                        text = "${dateFormat.format(Date(actividad.fechaInicio))} - ${dateFormat.format(Date(actividad.fechaFin))}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            IconButton(onClick = onEliminarClick) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevoProyectoDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, Color, Long, Long) -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var colorSeleccionado by remember { mutableStateOf(Color(0xFF2196F3)) }
    var fechaInicio by remember { mutableStateOf<Long?>(null) }
    var fechaFin by remember { mutableStateOf<Long?>(null) }
    var showDatePickerInicio by remember { mutableStateOf(false) }
    var showDatePickerFin by remember { mutableStateOf(false) }
    var errorFecha by remember { mutableStateOf("") }

    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val datePickerStateInicio = rememberDatePickerState()
    val datePickerStateFin = rememberDatePickerState()

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

                OutlinedButton(
                    onClick = { showDatePickerInicio = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.CalendarToday, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        fechaInicio?.let { "Inicio: ${dateFormat.format(Date(it))}" }
                            ?: "Seleccionar fecha de inicio"
                    )
                }

                OutlinedButton(
                    onClick = { showDatePickerFin = true },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = fechaInicio != null
                ) {
                    Icon(Icons.Default.CalendarToday, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        fechaFin?.let { "Fin: ${dateFormat.format(Date(it))}" }
                            ?: "Seleccionar fecha de fin"
                    )
                }

                if (errorFecha.isNotEmpty()) {
                    Text(
                        text = errorFecha,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

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
                onClick = {
                    if (fechaInicio != null && fechaFin != null) {
                        if (fechaFin!! > fechaInicio!!) {
                            onConfirm(nombre, descripcion, colorSeleccionado, fechaInicio!!, fechaFin!!)
                        } else {
                            errorFecha = "La fecha de fin debe ser posterior a la de inicio"
                        }
                    }
                },
                enabled = nombre.isNotBlank() && fechaInicio != null && fechaFin != null
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

    if (showDatePickerInicio) {
        DatePickerDialog(
            onDismissRequest = { showDatePickerInicio = false },
            confirmButton = {
                TextButton(onClick = {
                    fechaInicio = datePickerStateInicio.selectedDateMillis
                    showDatePickerInicio = false
                    errorFecha = ""
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePickerInicio = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerStateInicio)
        }
    }

    if (showDatePickerFin) {
        DatePickerDialog(
            onDismissRequest = { showDatePickerFin = false },
            confirmButton = {
                TextButton(onClick = {
                    fechaFin = datePickerStateFin.selectedDateMillis
                    showDatePickerFin = false
                    errorFecha = ""
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePickerFin = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerStateFin)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevaActividadDialog(
    proyecto: Proyecto,
    onDismiss: () -> Unit,
    onConfirm: (String, String, Long, Long) -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var fechaInicio by remember { mutableStateOf<Long?>(null) }
    var fechaFin by remember { mutableStateOf<Long?>(null) }
    var showDatePickerInicio by remember { mutableStateOf(false) }
    var showDatePickerFin by remember { mutableStateOf(false) }
    var errorFecha by remember { mutableStateOf("") }

    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val datePickerStateInicio = rememberDatePickerState()
    val datePickerStateFin = rememberDatePickerState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nueva Actividad") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Proyecto: ${proyecto.nombre}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Rango permitido: ${dateFormat.format(Date(proyecto.fechaInicio))} - ${dateFormat.format(Date(proyecto.fechaFin))}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )

                HorizontalDivider()

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

                OutlinedButton(
                    onClick = { showDatePickerInicio = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.CalendarToday, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        fechaInicio?.let { "Inicio: ${dateFormat.format(Date(it))}" }
                            ?: "Seleccionar fecha de inicio"
                    )
                }

                OutlinedButton(
                    onClick = { showDatePickerFin = true },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = fechaInicio != null
                ) {
                    Icon(Icons.Default.CalendarToday, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        fechaFin?.let { "Fin: ${dateFormat.format(Date(it))}" }
                            ?: "Seleccionar fecha de fin"
                    )
                }

                if (errorFecha.isNotEmpty()) {
                    Text(
                        text = errorFecha,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (fechaInicio != null && fechaFin != null) {
                        when {
                            fechaInicio!! < proyecto.fechaInicio || fechaInicio!! > proyecto.fechaFin -> {
                                errorFecha = "La fecha de inicio debe estar dentro del rango del proyecto"
                            }
                            fechaFin!! < proyecto.fechaInicio || fechaFin!! > proyecto.fechaFin -> {
                                errorFecha = "La fecha de fin debe estar dentro del rango del proyecto"
                            }
                            fechaFin!! <= fechaInicio!! -> {
                                errorFecha = "La fecha de fin debe ser posterior a la de inicio"
                            }
                            else -> {
                                onConfirm(nombre, descripcion, fechaInicio!!, fechaFin!!)
                            }
                        }
                    }
                },
                enabled = nombre.isNotBlank() && fechaInicio != null && fechaFin != null
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

    if (showDatePickerInicio) {
        DatePickerDialog(
            onDismissRequest = { showDatePickerInicio = false },
            confirmButton = {
                TextButton(onClick = {
                    fechaInicio = datePickerStateInicio.selectedDateMillis
                    showDatePickerInicio = false
                    errorFecha = ""
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePickerInicio = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerStateInicio)
        }
    }

    if (showDatePickerFin) {
        DatePickerDialog(
            onDismissRequest = { showDatePickerFin = false },
            confirmButton = {
                TextButton(onClick = {
                    fechaFin = datePickerStateFin.selectedDateMillis
                    showDatePickerFin = false
                    errorFecha = ""
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePickerFin = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerStateFin)
        }
    }
}