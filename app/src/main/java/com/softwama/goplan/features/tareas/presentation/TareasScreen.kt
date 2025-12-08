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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.softwama.goplan.R
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
    var tareaAEditar by remember { mutableStateOf<Tarea?>(null) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        stringResource(R.string.mis_tareas),
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.volver)
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true }
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = stringResource(R.string.agregar_tarea)
                )
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
                    label = {
                        Text(
                            "${stringResource(R.string.todas)} (${state.tareas.size})"
                        )
                    }
                )
                FilterChip(
                    selected = state.filtroActual == FiltroTarea.PENDIENTES,
                    onClick = { viewModel.cambiarFiltro(FiltroTarea.PENDIENTES) },
                    label = {
                        Text(
                            "${stringResource(R.string.pendientes)} (${state.tareas.count { !it.completada }})"
                        )
                    }
                )
                FilterChip(
                    selected = state.filtroActual == FiltroTarea.COMPLETADAS,
                    onClick = { viewModel.cambiarFiltro(FiltroTarea.COMPLETADAS) },
                    label = {
                        Text(
                            "${stringResource(R.string.completadas)} (${state.tareas.count { it.completada }})"
                        )
                    }
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
                            stringResource(R.string.no_hay_tareas),
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
                            onEditarClick = { tareaAEditar = tarea },
                            onEliminarClick = { viewModel.eliminarTarea(tarea.id) }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        TareaDialog(
            tarea = null,
            onDismiss = { showAddDialog = false },
            onConfirm = { titulo, descripcion, fecha ->
                viewModel.agregarTarea(titulo, descripcion, fecha)
                showAddDialog = false
            }
        )
    }

    tareaAEditar?.let { tarea ->
        TareaDialog(
            tarea = tarea,
            onDismiss = { tareaAEditar = null },
            onConfirm = { titulo, descripcion, fecha ->
                viewModel.editarTarea(tarea.id, titulo, descripcion, fecha)
                tareaAEditar = null
            }
        )
    }
}

@Composable
fun TareaItem(
    tarea: Tarea,
    onCompletarClick: () -> Unit,
    onEditarClick: () -> Unit,
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

            IconButton(onClick = onEditarClick) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = stringResource(R.string.editar),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            IconButton(onClick = onEliminarClick) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = stringResource(R.string.eliminar),
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TareaDialog(
    tarea: Tarea?,
    onDismiss: () -> Unit,
    onConfirm: (String, String, Long?) -> Unit
) {
    var titulo by remember { mutableStateOf(tarea?.titulo ?: "") }
    var descripcion by remember { mutableStateOf(tarea?.descripcion ?: "") }
    var fechaSeleccionada by remember { mutableStateOf(tarea?.fechaVencimiento) }
    var showDatePicker by remember { mutableStateOf(false) }
    var errorTitulo by remember { mutableStateOf<String?>(null) }
    var errorDescripcion by remember { mutableStateOf<String?>(null) }
    var advertenciaFecha by remember { mutableStateOf<String?>(null) }

    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val datePickerState = rememberDatePickerState()

    val MAX_TITULO_LENGTH = 20
    val MAX_DESCRIPCION_LENGTH = 50
    val MAX_SALTOS_LINEA_CONSECUTIVOS = 2

    // Mensajes desde resources, pero sin cambiar firma de funciones
    val maxCharsTituloMessage = stringResource(R.string.max_caracteres, MAX_TITULO_LENGTH)
    val maxCharsDescripcionMessage = stringResource(R.string.max_caracteres, MAX_DESCRIPCION_LENGTH)
    val fechaPasadaMessage = stringResource(R.string.fecha_pasada)

    fun validarTitulo(texto: String): String {
        var textoValidado = texto
        if (textoValidado.length > MAX_TITULO_LENGTH) {
            textoValidado = textoValidado.take(MAX_TITULO_LENGTH)
            errorTitulo = maxCharsTituloMessage
        } else {
            errorTitulo = null
        }
        return textoValidado
    }

    fun validarDescripcion(texto: String): String {
        var textoValidado = texto

        if (textoValidado.length > MAX_DESCRIPCION_LENGTH) {
            textoValidado = textoValidado.take(MAX_DESCRIPCION_LENGTH)
            errorDescripcion = maxCharsDescripcionMessage
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
            fechaPasadaMessage
        } else {
            null
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                if (tarea == null)
                    stringResource(R.string.nueva_tarea)
                else
                    stringResource(R.string.editar_tarea)
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = titulo,
                    onValueChange = { titulo = validarTitulo(it) },
                    label = { Text(stringResource(R.string.titulo)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    supportingText = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = errorTitulo ?: "",
                                color = MaterialTheme.colorScheme.error
                            )
                            Text(
                                text = "${titulo.length}/$MAX_TITULO_LENGTH",
                                color = if (titulo.length >= MAX_TITULO_LENGTH) {
                                    MaterialTheme.colorScheme.error
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            )
                        }
                    },
                    isError = errorTitulo != null
                )

                OutlinedTextField(
                    value = descripcion,
                    onValueChange = {
                        descripcion = validarDescripcion(it)
                    },
                    label = { Text(stringResource(R.string.descripcion_opcional)) },
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
                                "${stringResource(R.string.vence)} ${dateFormat.format(Date(it))}"
                            } ?: stringResource(R.string.seleccionar_fecha_vencimiento)
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
                Text(
                    if (tarea == null)
                        stringResource(R.string.agregar)
                    else
                        stringResource(R.string.guardar)
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancelar))
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
                    Text(stringResource(R.string.ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(stringResource(R.string.cancelar))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
