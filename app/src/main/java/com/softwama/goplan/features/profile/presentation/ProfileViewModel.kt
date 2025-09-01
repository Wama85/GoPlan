package com.softwama.goplan.features.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softwama.goplan.features.profile.domain.usecase.GetProfileUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(

    private val getProfileUseCase: GetProfileUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    init {
        loadProfileData()

    }
    fun loadProfileData() {
        viewModelScope.launch {
            getProfileUseCase().collect { result ->
                result.onSuccess { profile ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        userName = profile.name,
                        userEmail = profile.email,
                        avatarUrl = profile.avatarUrl
                    )
                }.onFailure { e ->
                    _state.value = _state.value.copy(
                        error = e.message ?: "Error al cargar perfil",
                        isLoading = false
                    )
                }
            }
        }
    }

}

data class ProfileState(
    val userName: String = "",
    val userEmail: String = "",
    val avatarUrl: String? = null,
    val dollarValue: Float? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)