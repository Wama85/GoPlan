package com.softwama.goplan.di

import com.google.firebase.messaging.FirebaseMessaging
import com.softwama.goplan.core.notifications.FirebaseNotificationManager
import com.softwama.goplan.core.notifications.data.repository.NotificationRepositoryImpl
import com.softwama.goplan.core.notifications.domain.repository.NotificationRepository
import com.softwama.goplan.core.notifications.domain.usecase.GetFcmTokenUseCase
import com.softwama.goplan.core.notifications.domain.usecase.GetNotificationPreferencesUseCase
import com.softwama.goplan.core.notifications.domain.usecase.HandleNotificationUseCase
import com.softwama.goplan.core.notifications.domain.usecase.SaveNotificationPreferencesUseCase
import com.softwama.goplan.core.notifications.domain.usecase.SubscribeToTopicUseCase
import com.softwama.goplan.core.notifications.presentation.NotificationsViewModel
import com.softwama.goplan.core.remoteconfig.RemoteConfigRepository
import com.softwama.goplan.data.local.datastore.UserPreferencesDataStore
import com.softwama.goplan.features.calendar.data.CalendarRepositoryImpl
import com.softwama.goplan.features.calendar.presentation.CalendarViewModel
import com.softwama.goplan.features.dashboard.DashboardViewModel
import com.softwama.goplan.features.login.data.LoginRepositoryImpl
import com.softwama.goplan.features.login.domain.repository.LoginRepository
import com.softwama.goplan.features.login.domain.usecase.LoginUseCase
import com.softwama.goplan.features.login.presentation.LoginViewModel
import com.softwama.goplan.features.maintenance.domain.CheckMaintenanceUseCase
import com.softwama.goplan.features.maintenance.presentation.MaintenanceViewModel
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

val appModule = module {

    single { RemoteConfigRepository() }
    single { UserPreferencesDataStore(get()) }
    single { FirebaseMessaging.getInstance() }
    single { FirebaseNotificationManager(get(), get()) }

    single<NotificationRepository> {
        NotificationRepositoryImpl(firebaseMessaging = get())
    }

    factory { GetFcmTokenUseCase(get()) }
    factory { SubscribeToTopicUseCase(get()) }
    factory { HandleNotificationUseCase() }
    factory { GetNotificationPreferencesUseCase(get()) }
    factory { SaveNotificationPreferencesUseCase(get()) }

    viewModel {
        NotificationsViewModel(
            getFcmTokenUseCase = get(),
            getNotificationPreferencesUseCase = get(),
            saveNotificationPreferencesUseCase = get(),
            subscribeToTopicUseCase = get()
        )
    }

    single<LoginRepository> { LoginRepositoryImpl() }
    single<ProfileRepository> { ProfileRepositoryImpl() }
    single<SuscribeRepository> { SuscribeRepositoryImpl() }
    single { CalendarRepositoryImpl(get()) }

    factory { LoginUseCase(get()) }
    factory { GetProfileUseCase(get()) }
    factory { GetSuscribeUseCase(get()) }
    factory { CheckMaintenanceUseCase(get()) }

    viewModel { LoginViewModel(get(), get(), get()) }
    viewModel { ProfileViewModel(get(), get()) }
    viewModel { SuscribeViewModel() }
    viewModel { DashboardViewModel() }
    viewModel { CalendarViewModel(get()) }
    viewModel { MaintenanceViewModel(get()) }
}