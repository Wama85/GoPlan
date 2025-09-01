package com.softwama.goplan.features.profile.domain.repository

import com.softwama.goplan.features.profile.domain.model.Profile
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    fun getProfile(): Flow<Result<Profile>>
}