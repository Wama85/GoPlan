package com.softwama.goplan.features.calendar.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.softwama.goplan.features.calendar.data.CalendarRepositoryImpl
import com.softwama.goplan.features.calendar.domain.model.CalendarEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CalendarState(
    val events: List<CalendarEvent> = emptyList(),
    val isLoading: Boolean = false,
    val selectedDate: String? = null,
    val error: String? = null,
    val isSignedIn: Boolean = false
)

class CalendarViewModel(
    private val repository: CalendarRepositoryImpl
) : ViewModel() {

    private val _state = MutableStateFlow(CalendarState())
    val state: StateFlow<CalendarState> = _state.asStateFlow()

    init {
        checkSignInStatus()
        loadEvents()
    }

    private fun checkSignInStatus() {
        _state.value = _state.value.copy(
            isSignedIn = repository.isSignedIn()
        )
    }

    fun loadEvents() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            repository.getEvents().collect { events ->
                _state.value = _state.value.copy(
                    events = events,
                    isLoading = false
                )
            }
        }
    }

    fun onSignInSuccess(account: GoogleSignInAccount?) {
        if (account != null) {
            _state.value = _state.value.copy(isSignedIn = true)
            loadEvents()
        }
    }

    fun signOut() {
        repository.signOut()
        _state.value = _state.value.copy(
            isSignedIn = false,
            events = emptyList()
        )
        loadEvents()
    }

    fun getSignInClient() = repository.getSignInClient()

    fun selectDate(date: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                selectedDate = date,
                isLoading = true
            )

            repository.getEventsForDate(date).collect { events ->
                _state.value = _state.value.copy(
                    events = events,
                    isLoading = false
                )
            }
        }
    }

    fun clearDateFilter() {
        loadEvents()
        _state.value = _state.value.copy(selectedDate = null)
    }
}