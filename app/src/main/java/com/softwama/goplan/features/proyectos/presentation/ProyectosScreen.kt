package com.softwama.goplan.features.proyectos.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.softwama.goplan.R
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
    var showDeleteConfirmation by remember { mutableStateOf<Proyecto?>(null) }
    var proyectoSeleccionado by remember { mutableStateOf<Proyecto?>(null) }
    var proyectoAEditar by remember { mutableStateOf<Proyecto?>(null) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        stringResource(R.string.mis_proyectos),
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.volver))
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true }
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.agregar_proyecto))
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
                        onClick = { proyectoSeleccionado = proyecto },
                        onEditClick = { proyectoAEditar = proyecto },
                        onDeleteClick = { showDeleteConfirmation = proyecto }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        ProyectoDialog(
            proyecto = null,
            onDismiss = { showAddDialog = false },
            onConfirm = { nombre, descripcion, fechaInicio, fechaFin, colorHex ->
                viewModel.agregarProyecto(
                    nombre,
                    descripcion,
                    Color(android.graphics.Color.parseColor(colorHex)),
                    fechaInicio,
                    fechaFin
                )
                showAddDialog = false
            }
        )
    }

    proyectoAEditar?.let { proyecto ->
        ProyectoDialog(
            proyecto = proyecto,
            onDismiss = { proyectoAEditar = null },
            onConfirm = { nombre, descripcion, fechaInicio, fechaFin, colorHex ->
                viewModel.editarProyecto(
                    proyecto.id,
                    nombre,
                    descripcion,
                    Color(android.graphics.Color.parseColor(colorHex)),
                    fechaInicio,
                    fechaFin
                )
                proyectoAEditar = null
            }
        )
    }

    proyectoSeleccionado?.let { proyecto ->
        DetalleProyectoDialog(
            proyecto = proyecto,
            onDismiss = { proyectoSeleccionado = null },
            viewModel = viewModel
        )
    }

    showDeleteConfirmation?.let { proyecto ->
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = null },
            title = { Text("Eliminar Proyecto") },
            text = { Text("¿Estás seguro de que deseas eliminar '${proyecto.nombre}'?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.eliminarProyecto(proyecto.id)
                        showDeleteConfirmation = null
                    }
                ) {
                    Text(stringResource(R.string.eliminar), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = null }) {
                    Text(stringResource(R.string.cancelar))
                }
            }
        )
    }
}

@Composable
fun ProyectoCard(
    proyecto: Proyecto,
    onClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = Color(android.graphics.Color.parseColor(proyecto.colorHex)).copy(alpha = 0.1f)
        )
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
                Text(
                    text = proyecto.nombre,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Row {
                    IconButton(onClick = onEditClick) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Editar",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = onDeleteClick) {
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
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = { proyecto.progreso },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = Color(android.graphics.Color.parseColor(proyecto.colorHex)),
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${(proyecto.progreso * 100).toInt()}% completado",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProyectoDialog(
    proyecto: Proyecto?,
    onDismiss: () -> Unit,
    onConfirm: (String, String, Long, Long, String) -> Unit
) {
    var nombre by remember { mutableStateOf(proyecto?.nombre ?: "") }
    var descripcion by remember { mutableStateOf(proyecto?.descripcion ?: "") }
    var fechaInicio by remember { mutableStateOf(proyecto?.fechaInicio) }
    var fechaFin by remember { mutableStateOf(proyecto?.fechaFin) }
    var colorSeleccionado by remember { mutableStateOf(proyecto?.colorHex ?: "#2196F3") }
    var showDatePickerInicio by remember { mutableStateOf(false) }
    var showDatePickerFin by remember { mutableStateOf(false) }
    var errorNombre by remember { mutableStateOf<String?>(null) }
    var errorDescripcion by remember { mutableStateOf<String?>(null) }
    var advertenciaFechaInicio by remember { mutableStateOf<String?>(null) }
    var advertenciaFechaFin by remember { mutableStateOf<String?>(null) }

    val MAX_NOMBRE_LENGTH = 20
    val MAX_DESCRIPCION_LENGTH = 50
    val MAX_SALTOS_LINEA_CONSECUTIVOS = 2

    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val datePickerStateInicio = rememberDatePickerState()
    val datePickerStateFin = rememberDatePickerState()

    val coloresDisponibles = listOf(
        "#2196F3" to "Azul",
        "#4CAF50" to "Verde",
        "#FF9800" to "Naranja",
        "#9C27B0" to "Morado",
        "#F44336" to "Rojo",
        "#00BCD4" to "Cyan"
    )

    fun validarNombre(texto: String): String {
        var textoValidado = texto
        if (textoValidado.length > MAX_NOMBRE_LENGTH) {
            textoValidado = textoValidado.take(MAX_NOMBRE_LENGTH)
            errorNombre = "Máximo $MAX_NOMBRE_LENGTH caracteres"
        } else {
            errorNombre = null
        }
        return textoValidado
    }

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
        title = { Text(if (proyecto == null) "Nuevo Proyecto" else "Editar Proyecto") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = validarNombre(it) },
                    label = { Text("Nombre del Proyecto") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    supportingText = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = errorNombre ?: "",
                                color = MaterialTheme.colorScheme.error
                            )
                            Text(
                                text = "${nombre.length}/$MAX_NOMBRE_LENGTH",
                                color = if (nombre.length >= MAX_NOMBRE_LENGTH) {
                                    MaterialTheme.colorScheme.error
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            )
                        }
                    },
                    isError = errorNombre != null
                )

                OutlinedTextField(
                    value = descripcion,
                    onValueChange = {
                        descripcion = validarDescripcion(it)
                    },
                    label = { Text("Descripción (opcional)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3,
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
                        onClick = { showDatePickerInicio = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.CalendarToday, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            fechaInicio?.let {
                                "Inicio: ${dateFormat.format(Date(it))}"
                            } ?: "Fecha de inicio"
                        )
                    }
                    advertenciaFechaInicio?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFFFA500),
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                    }
                }

                Column {
                    OutlinedButton(
                        onClick = { showDatePickerFin = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Event, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            fechaFin?.let {
                                "Fin: ${dateFormat.format(Date(it))}"
                            } ?: "Fecha de finalización"
                        )
                    }
                    advertenciaFechaFin?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFFFA500),
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                    }
                }

                Text(
                    text = "Color del proyecto:",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    coloresDisponibles.forEach { (colorHex, _) ->
                        Surface(
                            modifier = Modifier
                                .size(40.dp)
                                .clickable { colorSeleccionado = colorHex },
                            shape = RoundedCornerShape(8.dp),
                            color = Color(android.graphics.Color.parseColor(colorHex)),
                            border = if (colorSeleccionado == colorHex) {
                                androidx.compose.foundation.BorderStroke(
                                    3.dp,
                                    MaterialTheme.colorScheme.primary
                                )
                            } else null
                        ) {}
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (fechaInicio != null && fechaFin != null) {
                        onConfirm(
                            nombre.trim(),
                            descripcion.trim(),
                            fechaInicio!!,
                            fechaFin!!,
                            colorSeleccionado
                        )
                    }
                },
                enabled = nombre.isNotBlank() && fechaInicio != null && fechaFin != null
            ) {
                Text(if (proyecto == null) "Crear" else "Guardar")
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
                    advertenciaFechaInicio = validarFecha(fechaInicio)
                    showDatePickerInicio = false
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
                    advertenciaFechaFin = validarFecha(fechaFin)
                    showDatePickerFin = false
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
fun DetalleProyectoDialog(
    proyecto: Proyecto,
    onDismiss: () -> Unit,
    viewModel: ProyectosViewModel
) {
    val state by viewModel.state.collectAsState()
    val actividades = state.actividadesPorProyecto[proyecto.id] ?: emptyList()
    var showAddActividadDialog by remember { mutableStateOf(false) }
    var showDeleteActividadConfirmation by remember { mutableStateOf<String?>(null) }
    var actividadAEditar by remember { mutableStateOf<Actividad?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(proyecto.nombre)
                LinearProgressIndicator(
                    progress = { proyecto.progreso },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    color = Color(android.graphics.Color.parseColor(proyecto.colorHex))
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (proyecto.descripcion.isNotEmpty()) {
                    Text(
                        text = proyecto.descripcion,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                HorizontalDivider()

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Actividades",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = { showAddActividadDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Agregar Actividad")
                    }
                }

                if (actividades.isEmpty()) {
                    Text(
                        "No hay actividades",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        actividades.forEach { actividad ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (actividad.completada)
                                        Color(0xFFE8F5E9)
                                    else
                                        MaterialTheme.colorScheme.surface
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = actividad.completada,
                                        onCheckedChange = {
                                            viewModel.toggleActividadCompletada(actividad)
                                        }
                                    )
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = actividad.nombre,
                                            style = MaterialTheme.typography.bodyMedium,
                                            textDecoration = if (actividad.completada)
                                                androidx.compose.ui.text.style.TextDecoration.LineThrough
                                            else null
                                        )
                                        if (actividad.descripcion.isNotEmpty()) {
                                            Text(
                                                text = actividad.descripcion,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurface.copy(
                                                    alpha = 0.6f
                                                )
                                            )
                                        }
                                    }
                                    IconButton(onClick = {
                                        actividadAEditar = actividad
                                    }) {
                                        Icon(
                                            Icons.Default.Edit,
                                            contentDescription = "Editar",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    IconButton(onClick = {
                                        showDeleteActividadConfirmation = actividad.id
                                    }) {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = "Eliminar",
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )

    if (showAddActividadDialog) {
        ActividadDialog(
            actividad = null,
            proyectoId = proyecto.id,
            onDismiss = { showAddActividadDialog = false },
            onConfirm = { nombre, descripcion, fechaInicio, fechaFin ->
                viewModel.agregarActividad(
                    proyecto.id,
                    nombre,
                    descripcion,
                    fechaInicio,
                    fechaFin
                )
                showAddActividadDialog = false
            }
        )
    }

    actividadAEditar?.let { actividad ->
        ActividadDialog(
            actividad = actividad,
            proyectoId = proyecto.id,
            onDismiss = { actividadAEditar = null },
            onConfirm = { nombre, descripcion, fechaInicio, fechaFin ->
                viewModel.editarActividad(
                    actividad.id,
                    nombre,
                    descripcion,
                    fechaInicio,
                    fechaFin
                )
                actividadAEditar = null
            }
        )
    }

    showDeleteActividadConfirmation?.let { actividadId ->
        AlertDialog(
            onDismissRequest = { showDeleteActividadConfirmation = null },
            title = { Text("Eliminar Actividad") },
            text = { Text("¿Estás seguro de que deseas eliminar esta actividad?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.eliminarActividad(actividadId)
                        showDeleteActividadConfirmation = null
                    }
                ) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteActividadConfirmation = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActividadDialog(
    actividad: Actividad?,
    proyectoId: String,
    onDismiss: () -> Unit,
    onConfirm: (String, String, Long, Long) -> Unit
) {
    var nombre by remember { mutableStateOf(actividad?.nombre ?: "") }
    var descripcion by remember { mutableStateOf(actividad?.descripcion ?: "") }
    var fechaInicio by remember { mutableStateOf(actividad?.fechaInicio) }
    var fechaFin by remember { mutableStateOf(actividad?.fechaFin) }
    var showDatePickerInicio by remember { mutableStateOf(false) }
    var showDatePickerFin by remember { mutableStateOf(false) }
    var errorNombre by remember { mutableStateOf<String?>(null) }
    var errorDescripcion by remember { mutableStateOf<String?>(null) }
    var advertenciaFechaInicio by remember { mutableStateOf<String?>(null) }
    var advertenciaFechaFin by remember { mutableStateOf<String?>(null) }

    val MAX_NOMBRE_LENGTH = 20
    val MAX_DESCRIPCION_LENGTH = 20
    val MAX_SALTOS_LINEA_CONSECUTIVOS = 2

    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val datePickerStateInicio = rememberDatePickerState()
    val datePickerStateFin = rememberDatePickerState()

    fun validarNombre(texto: String): String {
        var textoValidado = texto
        if (textoValidado.length > MAX_NOMBRE_LENGTH) {
            textoValidado = textoValidado.take(MAX_NOMBRE_LENGTH)
            errorNombre = "Máximo $MAX_NOMBRE_LENGTH caracteres"
        } else {
            errorNombre = null
        }
        return textoValidado
    }

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
        title = { Text(if (actividad == null) "Nueva Actividad" else "Editar Actividad") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = validarNombre(it) },
                    label = { Text("Nombre de la Actividad") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    supportingText = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = errorNombre ?: "",
                                color = MaterialTheme.colorScheme.error
                            )
                            Text(
                                text = "${nombre.length}/$MAX_NOMBRE_LENGTH",
                                color = if (nombre.length >= MAX_NOMBRE_LENGTH) {
                                    MaterialTheme.colorScheme.error
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            )
                        }
                    },
                    isError = errorNombre != null
                )

                OutlinedTextField(
                    value = descripcion,
                    onValueChange = {
                        descripcion = validarDescripcion(it)
                    },
                    label = { Text("Descripción (opcional)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3,
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
                        onClick = { showDatePickerInicio = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.CalendarToday, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            fechaInicio?.let {
                                "Inicio: ${dateFormat.format(Date(it))}"
                            } ?: "Fecha de inicio"
                        )
                    }
                    advertenciaFechaInicio?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFFFA500),
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                    }
                }

                Column {
                    OutlinedButton(
                        onClick = { showDatePickerFin = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Event, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            fechaFin?.let {
                                "Fin: ${dateFormat.format(Date(it))}"
                            } ?: "Fecha de finalización"
                        )
                    }
                    advertenciaFechaFin?.let {
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
                onClick = {
                    if (fechaInicio != null && fechaFin != null) {
                        onConfirm(nombre.trim(), descripcion.trim(), fechaInicio!!, fechaFin!!)
                    }
                },
                enabled = nombre.isNotBlank() && fechaInicio != null && fechaFin != null
            ) {
                Text(if (actividad == null) "Agregar" else "Guardar")
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
                    advertenciaFechaInicio = validarFecha(fechaInicio)
                    showDatePickerInicio = false
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
                    advertenciaFechaFin = validarFecha(fechaFin)
                    showDatePickerFin = false
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
