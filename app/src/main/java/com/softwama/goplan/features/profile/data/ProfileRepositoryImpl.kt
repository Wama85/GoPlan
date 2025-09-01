package com.softwama.goplan.features.profile.data

import com.softwama.goplan.features.profile.domain.model.Profile
import com.softwama.goplan.features.profile.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ProfileRepositoryImpl : ProfileRepository {
    override fun getProfile(): Flow<Result<Profile>> = flow {
        // Simulamos datos de perfil por ahora
        val avatarUrls = listOf(
            "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=500&q=80", // Persona 1
            "https://images.unsplash.com/photo-1633332755192-727a05c4013d?w=500&q=80", // Persona 2
            "https://images.unsplash.com/photo-1603415526960-f7e0328c63b1?w=500&q=80", // Persona 3
            "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=500&q=80", // Persona 4
            "https://images.unsplash.com/photo-1580489944761-15a19d654956?w=500&q=80"  // Persona 5
        )
        val profile = Profile(
            id = "user_123",
            name = "Usuario Demo",
            email = "usuario@ejemplo.com",
            avatarUrl = avatarUrls.random(),

            )
        emit(Result.success(profile))
    }
}