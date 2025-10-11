package com.softwama.goplan.features.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softwama.goplan.data.local.datastore.UserPreferencesDataStore
import com.softwama.goplan.features.profile.domain.usecase.GetProfileUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class ProfileState(
    val userName: String = "",
    val userEmail: String = "",
    val isLoading: Boolean = false
)

class ProfileViewModel(
    private val getProfileUseCase: GetProfileUseCase,
    private val dataStore: UserPreferencesDataStore
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            val userName = dataStore.getUserName().first() ?: ""
            val userEmail = dataStore.getUserEmail().first() ?: ""

            _state.value = _state.value.copy(
                userName = userName,
                userEmail = userEmail,
                isLoading = false
            )
        }
    }

    fun logout() {
        viewModelScope.launch {
            dataStore.clearSession()
        }
    }
}