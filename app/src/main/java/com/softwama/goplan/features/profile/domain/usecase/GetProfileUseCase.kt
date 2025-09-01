package com.softwama.goplan.features.profile.domain.usecase

import com.softwama.goplan.features.profile.domain.model.Profile
import com.softwama.goplan.features.profile.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow

class GetProfileUseCase(
    private val profileRepository: ProfileRepository
) {
    operator fun invoke(): Flow<Result<Profile>> {
        return profileRepository.getProfile()
    }
}