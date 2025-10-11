package com.softwama.goplan.features.calendar.data

import android.content.Context
import com.google.api.client.util.DateTime
import com.softwama.goplan.features.calendar.domain.model.CalendarEvent
import com.softwama.goplan.features.calendar.domain.model.EventColor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class CalendarRepositoryImpl(private val context: Context) {

    private val authManager = GoogleAuthManager(context)

    // Datos MOCK - Simulamos eventos del calendario
    private val mockEvents = listOf(
        CalendarEvent(
            id = "1",
            title = "Reunión de equipo",
            description = "Reunión semanal con el equipo de desarrollo",
            startTime = "2025-10-15T10:00:00",
            endTime = "2025-10-15T11:00:00",
            location = "Sala de conferencias A",
            color = EventColor.BLUE
        ),
        CalendarEvent(
            id = "2",
            title = "Almuerzo con cliente",
            description = "Presentación de proyecto nuevo",
            startTime = "2025-10-15T13:00:00",
            endTime = "2025-10-15T14:30:00",
            location = "Restaurante El Buen Sabor",
            color = EventColor.GREEN
        ),
        CalendarEvent(
            id = "3",
            title = "Revisión de código",
            description = "Code review del módulo de tareas",
            startTime = "2025-10-16T15:00:00",
            endTime = "2025-10-16T16:00:00",
            location = "Online - Google Meet",
            color = EventColor.ORANGE
        ),
        CalendarEvent(
            id = "4",
            title = "Entrenamiento en el gym",
            description = "Rutina de piernas",
            startTime = "2025-10-17T18:00:00",
            endTime = "2025-10-17T19:30:00",
            location = "PowerGym",
            color = EventColor.RED
        ),
        CalendarEvent(
            id = "5",
            title = "Cita médica",
            description = "Chequeo general",
            startTime = "2025-10-18T09:00:00",
            endTime = "2025-10-18T10:00:00",
            location = "Clínica Santa María",
            color = EventColor.PURPLE
        ),
        CalendarEvent(
            id = "6",
            title = "Presentación proyecto",
            description = "Demo del MVP a stakeholders",
            startTime = "2025-10-20T14:00:00",
            endTime = "2025-10-20T15:30:00",
            location = "Sala principal",
            color = EventColor.BLUE
        ),
        CalendarEvent(
            id = "7",
            title = "Cumpleaños de Ana",
            description = "Fiesta de cumpleaños",
            startTime = "2025-10-22T19:00:00",
            endTime = "2025-10-22T23:00:00",
            location = "Casa de Ana",
            color = EventColor.GREEN
        ),
        CalendarEvent(
            id = "8",
            title = "Dentista",
            description = "Limpieza dental",
            startTime = "2025-10-25T16:00:00",
            endTime = "2025-10-25T17:00:00",
            location = "Consultorio Dr. Pérez",
            color = EventColor.RED
        )
    )

    fun getEvents(): Flow<List<CalendarEvent>> = flow {
        delay(500)

        val account = authManager.getLastSignedInAccount()

        if (account != null) {
            // Usuario autenticado - obtener eventos reales de Google Calendar
            try {
                val events = getGoogleCalendarEvents()
                emit(events)
            } catch (e: Exception) {
                // Si falla, usar datos mock
                emit(mockEvents)
            }
        } else {
            // No autenticado - usar datos mock
            emit(mockEvents)
        }
    }

    private suspend fun getGoogleCalendarEvents(): List<CalendarEvent> = withContext(Dispatchers.IO) {
        val account = authManager.getLastSignedInAccount() ?: return@withContext emptyList()
        val service = authManager.getCalendarService(account)

        try {
            // Obtener eventos del calendario primario
            val now = DateTime(System.currentTimeMillis())
            val events = service.events().list("primary")
                .setMaxResults(50)
                .setTimeMin(now)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute()

            events.items?.map { event ->
                CalendarEvent(
                    id = event.id ?: "",
                    title = event.summary ?: "Sin título",
                    description = event.description ?: "",
                    startTime = convertDateTime(event.start?.dateTime ?: event.start?.date),
                    endTime = convertDateTime(event.end?.dateTime ?: event.end?.date),
                    location = event.location,
                    color = getRandomColor()
                )
            } ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun convertDateTime(dateTime: DateTime?): String {
        if (dateTime == null) return LocalDateTime.now().toString()

        return try {
            val instant = Instant.ofEpochMilli(dateTime.value)
            val localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
            localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        } catch (e: Exception) {
            LocalDateTime.now().toString()
        }
    }

    private fun getRandomColor(): EventColor {
        return EventColor.values().random()
    }

    fun getEventById(id: String): Flow<CalendarEvent?> = flow {
        delay(300)
        emit(mockEvents.find { it.id == id })
    }

    fun getEventsForDate(date: String): Flow<List<CalendarEvent>> = flow {
        delay(300)
        val filtered = mockEvents.filter { it.startTime.startsWith(date) }
        emit(filtered)
    }

    fun isSignedIn(): Boolean = authManager.isSignedIn()

    fun getSignInClient() = authManager.googleSignInClient

    fun signOut() = authManager.signOut()
}