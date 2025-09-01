package com.softwama.goplan.di

import com.softwama.goplan.features.login.data.LoginRepositoryImpl
import com.softwama.goplan.features.login.domain.repository.LoginRepository
import com.softwama.goplan.features.login.domain.usecase.LoginUseCase
import com.softwama.goplan.features.login.presentation.LoginViewModel
import com.softwama.goplan.features.profile.data.ProfileRepositoryImpl
import com.softwama.goplan.features.profile.domain.repository.ProfileRepository
import com.softwama.goplan.features.profile.domain.usecase.GetProfileUseCase
import com.softwama.goplan.features.profile.presentation.ProfileViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule=module{
    //Repositories
    single<LoginRepository> { LoginRepositoryImpl() }
    single<ProfileRepository> { ProfileRepositoryImpl() }

    //UseCase
    factory { LoginUseCase(get()) }
    factory { GetProfileUseCase(get()) }

    //ViewModels
    viewModel { LoginViewModel(get()) }
    viewModel { ProfileViewModel(get()) }
}