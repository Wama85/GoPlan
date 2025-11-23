package com.softwama.goplan.features.tareas.presentation

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
import com.softwama.goplan.features.tareas.domain.model.Tarea
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TareasScreen(
    navController: NavController,
    viewModel: TareasViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Mis Tareas",
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
                Icon(Icons.Default.Add, contentDescription = "Agregar Tarea")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = state.filtroActual == FiltroTarea.TODAS,
                    onClick = { viewModel.cambiarFiltro(FiltroTarea.TODAS) },
                    label = { Text("Todas (${state.tareas.size})") }
                )
                FilterChip(
                    selected = state.filtroActual == FiltroTarea.PENDIENTES,
                    onClick = { viewModel.cambiarFiltro(FiltroTarea.PENDIENTES) },
                    label = { Text("Pendientes (${state.tareas.count { !it.completada }})") }
                )
                FilterChip(
                    selected = state.filtroActual == FiltroTarea.COMPLETADAS,
                    onClick = { viewModel.cambiarFiltro(FiltroTarea.COMPLETADAS) },
                    label = { Text("Completadas (${state.tareas.count { it.completada }})") }
                )
            }

            if (state.tareasFiltradas.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.TaskAlt,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                        )
                        Text(
                            "No hay tareas",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.tareasFiltradas) { tarea ->
                        TareaItem(
                            tarea = tarea,
                            onCompletarClick = { viewModel.toggleCompletada(tarea.id) },
                            onEliminarClick = { viewModel.eliminarTarea(tarea.id) }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AgregarTareaDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { titulo, descripcion, fecha ->
                viewModel.agregarTarea(titulo, descripcion, fecha)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun TareaItem(
    tarea: Tarea,
    onCompletarClick: () -> Unit,
    onEliminarClick: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val isVencida = tarea.fechaVencimiento?.let { it < System.currentTimeMillis() } ?: false

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = if (isVencida && !tarea.completada) {
            CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
        } else {
            CardDefaults.cardColors()
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = tarea.completada,
                onCheckedChange = { onCompletarClick() }
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = tarea.titulo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textDecoration = if (tarea.completada) {
                        androidx.compose.ui.text.style.TextDecoration.LineThrough
                    } else null
                )
                if (tarea.descripcion.isNotEmpty()) {
                    Text(
                        text = tarea.descripcion,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
                tarea.fechaVencimiento?.let { fecha ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            Icons.Default.CalendarToday,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = if (isVencida && !tarea.completada) Color(0xFFD32F2F) else MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = dateFormat.format(Date(fecha)),
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isVencida && !tarea.completada) Color(0xFFD32F2F) else MaterialTheme.colorScheme.primary
                        )
                    }
                }
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
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarTareaDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, Long?) -> Unit
) {
    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var fechaSeleccionada by remember { mutableStateOf<Long?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    var errorDescripcion by remember { mutableStateOf<String?>(null) }
    var advertenciaFecha by remember { mutableStateOf<String?>(null) }

    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val datePickerState = rememberDatePickerState()

    val MAX_DESCRIPCION_LENGTH = 100
    val MAX_SALTOS_LINEA_CONSECUTIVOS = 2

    fun validarDescripcion(texto: String): String {
        var textoValidado = texto

        if (textoValidado.length > MAX_DESCRIPCION_LENGTH) {
            textoValidado = textoValidado.take(MAX_DESCRIPCION_LENGTH)
            errorDescripcion = "Máximo $MAX_DESCRIPCION_LENGTH caracteres"
        } else {
            errorDescripcion = null
        }

        val regex = Regex("\n{${MAX_SALTOS_LINEA_CONSECUTIVOS + 1},}")
        textoValidado = textoValidado.replace(regex, "\n".repeat(MAX_SALTOS_LINEA_CONSECUTIVOS))

        return textoValidado
    }

    fun validarFecha(fecha: Long?): String? {
        if (fecha == null) return null
        val hoy = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        return if (fecha < hoy) {
            "⚠️ Fecha pasada"
        } else {
            null
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nueva Tarea") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = titulo,
                    onValueChange = { titulo = it },
                    label = { Text("Título") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = descripcion,
                    onValueChange = {
                        descripcion = validarDescripcion(it)
                    },
                    label = { Text("Descripción (opcional)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 5,
                    supportingText = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = errorDescripcion ?: "",
                                color = MaterialTheme.colorScheme.error
                            )
                            Text(
                                text = "${descripcion.length}/$MAX_DESCRIPCION_LENGTH",
                                color = if (descripcion.length >= MAX_DESCRIPCION_LENGTH) {
                                    MaterialTheme.colorScheme.error
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            )
                        }
                    },
                    isError = errorDescripcion != null
                )

                Column {
                    OutlinedButton(
                        onClick = { showDatePicker = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.CalendarToday, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            fechaSeleccionada?.let {
                                "Vence: ${dateFormat.format(Date(it))}"
                            } ?: "Seleccionar fecha de vencimiento"
                        )
                    }
                    advertenciaFecha?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFFFA500),
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(titulo.trim(), descripcion.trim(), fechaSeleccionada) },
                enabled = titulo.isNotBlank()
            ) {
                Text("Agregar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    fechaSeleccionada = datePickerState.selectedDateMillis
                    advertenciaFecha = validarFecha(fechaSeleccionada)
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}