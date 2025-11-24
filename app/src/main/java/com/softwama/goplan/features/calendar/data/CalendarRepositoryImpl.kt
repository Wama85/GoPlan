package com.softwama.goplan.features.calendar.data

import android.content.Context
import android.util.Log
import com.google.api.client.util.DateTime
import com.softwama.goplan.data.local.datastore.UserPreferencesDataStore
import com.softwama.goplan.features.calendar.domain.model.CalendarEvent
import com.softwama.goplan.features.calendar.domain.model.EventColor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class CalendarRepositoryImpl(
    private val context: Context,
    private val authManager: GoogleAuthManager,
    private val dataStore: UserPreferencesDataStore  // ← INYECTAR EN VEZ DE CREAR
) {

    companion object {
        private const val TAG = "CalendarRepository"
    }

    // ← ELIMINAR ESTA LÍNEA: private val dataStore = UserPreferencesDataStore(context)

    private val mockEvents = listOf(
        CalendarEvent(
            id = "1",
            title = "Reunión de equipo",
            description = "Reunión semanal con el equipo de desarrollo",
            startTime = "2025-11-25T10:00:00",
            endTime = "2025-11-25T11:00:00",
            location = "Sala de conferencias A",
            color = EventColor.BLUE
        ),
        CalendarEvent(
            id = "2",
            title = "Almuerzo con cliente",
            description = "Presentación de proyecto nuevo",
            startTime = "2025-11-25T13:00:00",
            endTime = "2025-11-25T14:30:00",
            location = "Restaurante El Buen Sabor",
            color = EventColor.GREEN
        )
    )

    fun getEvents(): Flow<List<CalendarEvent>> = flow {
        delay(500)

        val loginType = dataStore.getLoginType().first()
        Log.d(TAG, "Login type: $loginType")

        if (loginType == "google") {
            val account = authManager.getLastSignedInAccount()
            Log.d(TAG, "Getting events. Account: ${account?.email}")

            if (account != null) {
                try {
                    Log.d(TAG, "Attempting to fetch Google Calendar events...")
                    val events = getGoogleCalendarEvents()
                    Log.d(TAG, "Fetched ${events.size} events from Google Calendar")

                    if (events.isNotEmpty()) {
                        emit(events)
                    } else {
                        Log.w(TAG, "No events found in Google Calendar, using mock data")
                        emit(mockEvents)
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error fetching Google Calendar events: ${e.message}", e)
                    emit(mockEvents)
                }
            } else {
                Log.w(TAG, "No Google account signed in, using mock data")
                emit(mockEvents)
            }
        } else {
            Log.w(TAG, "Firebase login detected, using mock data only")
            emit(mockEvents)
        }
    }

    private suspend fun getGoogleCalendarEvents(): List<CalendarEvent> = withContext(Dispatchers.IO) {
        val account = authManager.getLastSignedInAccount()
        if (account == null) {
            Log.e(TAG, "No account available for calendar service")
            return@withContext emptyList()
        }

        Log.d(TAG, "Building calendar service for account: ${account.email}")

        try {
            val service = authManager.getCalendarService(account)
            Log.d(TAG, "Calendar service created successfully")

            val now = DateTime(System.currentTimeMillis())
            Log.d(TAG, "Fetching events from primary calendar starting at: $now")

            val events = service.events().list("primary")
                .setMaxResults(50)
                .setTimeMin(now)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute()

            Log.d(TAG, "API call successful. Events found: ${events.items?.size ?: 0}")

            val mappedEvents = events.items?.map { event ->
                Log.d(TAG, "Event: ${event.summary} at ${event.start?.dateTime ?: event.start?.date}")
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

            Log.d(TAG, "Returning ${mappedEvents.size} mapped events")
            return@withContext mappedEvents

        } catch (e: Exception) {
            Log.e(TAG, "Exception in getGoogleCalendarEvents: ${e.javaClass.simpleName} - ${e.message}", e)
            return@withContext emptyList()
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

    fun isSignedIn(): Boolean {
        val signedIn = authManager.isSignedIn()
        Log.d(TAG, "isSignedIn: $signedIn")
        return signedIn
    }

    fun getSignInClient() = authManager.googleSignInClient

    fun signOut() {
        Log.d(TAG, "Signing out")
        authManager.signOut()
    }
}