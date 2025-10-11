package com.softwama.goplan.di

import com.softwama.goplan.features.calendar.data.CalendarRepositoryImpl
import com.softwama.goplan.features.calendar.presentation.CalendarViewModel
import com.softwama.goplan.features.dashboard.DashboardViewModel
import com.softwama.goplan.features.login.data.LoginRepositoryImpl
import com.softwama.goplan.features.login.domain.repository.LoginRepository
import com.softwama.goplan.features.login.domain.usecase.LoginUseCase
import com.softwama.goplan.features.login.presentation.LoginViewModel
import com.softwama.goplan.features.profile.data.ProfileRepositoryImpl
import com.softwama.goplan.features.profile.domain.repository.ProfileRepository
import com.softwama.goplan.features.profile.domain.usecase.GetProfileUseCase
import com.softwama.goplan.features.profile.presentation.ProfileViewModel
import com.softwama.goplan.features.suscribe.data.SuscribeRepositoryImpl
import com.softwama.goplan.features.suscribe.domain.repository.SuscribeRepository
import com.softwama.goplan.features.suscribe.domain.usecase.GetSuscribeUseCase
import com.softwama.goplan.features.suscribe.presentation.SuscribeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule=module{
    //Repositories
    single<LoginRepository> { LoginRepositoryImpl() }
    single<ProfileRepository> { ProfileRepositoryImpl() }
    single<SuscribeRepository> { SuscribeRepositoryImpl() }
    single { CalendarRepositoryImpl(get ()) }

    //UseCase
    factory { LoginUseCase(get()) }
    factory { GetProfileUseCase(get()) }
    factory { GetSuscribeUseCase(get()) }

    //ViewModels
    viewModel { LoginViewModel(get()) }
    viewModel { ProfileViewModel(get()) }
    viewModel { SuscribeViewModel() }
    viewModel { DashboardViewModel() }
    viewModel { CalendarViewModel(get()) }
}