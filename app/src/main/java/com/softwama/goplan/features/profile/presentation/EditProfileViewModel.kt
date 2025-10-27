package com.softwama.goplan.features.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softwama.goplan.data.local.datastore.UserPreferencesDataStore
import com.softwama.goplan.features.profile.domain.usecase.UpdateProfileUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class EditProfileState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val message: String? = null,
    val isSaving: Boolean = false
)

class EditProfileViewModel(
    private val dataStore: UserPreferencesDataStore,
    private val updateProfileUseCase: UpdateProfileUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(EditProfileState())
    val state: StateFlow<EditProfileState> = _state.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            val name = dataStore.getUserName().first() ?: ""
            val email = dataStore.getUserEmail().first() ?: ""
            _state.value = _state.value.copy(name = name, email = email)
        }
    }

    fun onNameChange(value: String) {
        _state.value = _state.value.copy(name = value)
    }

    fun onEmailChange(value: String) {
        _state.value = _state.value.copy(email = value)
    }

    fun onPasswordChange(value: String) {
        _state.value = _state.value.copy(password = value)
    }

    fun saveChanges() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isSaving = true)
            updateProfileUseCase(
                name = _state.value.name,
                email = _state.value.email,
                password = _state.value.password.ifBlank { null }
            )
            _state.value = _state.value.copy(
                isSaving = false,
                message = "Cambios guardados correctamente"
            )
        }
    }
}
