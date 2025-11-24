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
import com.softwama.goplan.data.local.database.GoPlanDatabase
import com.softwama.goplan.data.local.datastore.UserPreferencesDataStore
import com.softwama.goplan.features.calendar.data.CalendarRepositoryImpl
import com.softwama.goplan.features.calendar.data.GoogleAuthManager
import com.softwama.goplan.features.calendar.presentation.CalendarViewModel
import com.softwama.goplan.features.dashboard.DashboardViewModel
import com.softwama.goplan.features.estadisticas.data.repository.EstadisticaRepositoryImpl
import com.softwama.goplan.features.estadisticas.domain.repository.EstadisticaRepository
import com.softwama.goplan.features.estadisticas.domain.usecase.ObtenerEstadisticasUseCase
import com.softwama.goplan.features.estadisticas.presentation.EstadisticasViewModel
import com.softwama.goplan.features.login.data.repository.LoginRepositoryImpl
import com.softwama.goplan.features.login.domain.repository.LoginRepository
import com.softwama.goplan.features.login.domain.usecase.LoginUseCase
import com.softwama.goplan.features.login.presentation.LoginViewModel
import com.softwama.goplan.features.maintenance.domain.CheckMaintenanceUseCase
import com.softwama.goplan.features.maintenance.presentation.MaintenanceViewModel
import com.softwama.goplan.features.profile.data.ProfileRepositoryImpl
import com.softwama.goplan.features.profile.domain.repository.ProfileRepository
import com.softwama.goplan.features.profile.domain.usecase.GetProfileUseCase
import com.softwama.goplan.features.profile.presentation.ProfileViewModel
import com.softwama.goplan.features.proyectos.data.repository.ActividadRepositoryImpl
import com.softwama.goplan.features.proyectos.data.repository.ProyectoRepositoryImpl
import com.softwama.goplan.features.proyectos.domain.repository.ActividadRepository
import com.softwama.goplan.features.proyectos.domain.repository.ProyectoRepository
import com.softwama.goplan.features.proyectos.domain.usecase.ActividadUseCases
import com.softwama.goplan.features.proyectos.domain.usecase.ActualizarActividadUseCase
import com.softwama.goplan.features.proyectos.domain.usecase.ActualizarProyectoUseCase
import com.softwama.goplan.features.proyectos.domain.usecase.CrearActividadUseCase
import com.softwama.goplan.features.proyectos.domain.usecase.CrearProyectoUseCase
import com.softwama.goplan.features.proyectos.domain.usecase.EliminarActividadUseCase
import com.softwama.goplan.features.proyectos.domain.usecase.EliminarProyectoUseCase
import com.softwama.goplan.features.proyectos.domain.usecase.ObtenerActividadesUseCase
import com.softwama.goplan.features.proyectos.domain.usecase.ObtenerProyectosUseCase
import com.softwama.goplan.features.proyectos.domain.usecase.ProyectoUseCases
import com.softwama.goplan.features.proyectos.presentation.ProyectosViewModel
import com.softwama.goplan.features.suscribe.data.SuscribeRepositoryImpl
import com.softwama.goplan.features.suscribe.domain.repository.SuscribeRepository
import com.softwama.goplan.features.suscribe.domain.usecase.GetSuscribeUseCase
import com.softwama.goplan.features.suscribe.domain.usecase.RegistrarUsuarioUseCase
import com.softwama.goplan.features.suscribe.presentation.SuscribeViewModel
import com.softwama.goplan.features.tareas.data.repository.TareaRepositoryImpl
import com.softwama.goplan.features.tareas.domain.repository.TareaRepository
import com.softwama.goplan.features.tareas.domain.usecase.*
import com.softwama.goplan.features.tareas.presentation.TareasViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    single { GoPlanDatabase.getInstance(get()) }
    single { get<GoPlanDatabase>().tareaDao() }
    single { get<GoPlanDatabase>().proyectoDao() }
    single { get<GoPlanDatabase>().actividadDao() }

    single { RemoteConfigRepository() }
    single { UserPreferencesDataStore(get()) }
    single { FirebaseMessaging.getInstance() }
    single { FirebaseNotificationManager(get(), get()) }

    // ← AGREGAR ESTO
    single { GoogleAuthManager(get()) }

    single<NotificationRepository> {
        NotificationRepositoryImpl(
            firebaseMessaging = get(),
            dataStore = get()
        )
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
    single { CalendarRepositoryImpl(get(), get(),get()) }  // ← Inyectar GoogleAuthManager

    factory { LoginUseCase(get()) }
    factory { GetProfileUseCase(get()) }
    factory { GetSuscribeUseCase(get()) }
    factory { CheckMaintenanceUseCase(get()) }
    factory { RegistrarUsuarioUseCase(get()) }

    single<TareaRepository> { TareaRepositoryImpl(get(),null,get()) }
    factory { ObtenerTareasUseCase(get()) }
    factory { CrearTareaUseCase(get()) }
    factory { ActualizarTareaUseCase(get()) }
    factory { EliminarTareaUseCase(get()) }
    factory {
        TareaUseCases(
            obtenerTareasUseCase = get(),
            crearTareaUseCase = get(),
            actualizarTareaUseCase = get(),
            eliminarTareaUseCase = get()
        )
    }

    viewModel { LoginViewModel(get(), get(), get()) }
    viewModel { ProfileViewModel(get(), get()) }
    viewModel { SuscribeViewModel(get()) }
    viewModel { DashboardViewModel(get()) }
    viewModel { CalendarViewModel(get()) }
    viewModel { MaintenanceViewModel(get()) }
    viewModel { TareasViewModel(get()) }

    single<ProyectoRepository> { ProyectoRepositoryImpl(get(),null,get()) }
    factory { ObtenerProyectosUseCase(get()) }
    factory { CrearProyectoUseCase(get()) }
    factory { ActualizarProyectoUseCase(get()) }
    factory { EliminarProyectoUseCase(get()) }
    factory {
        ProyectoUseCases(
            obtenerProyectosUseCase = get(),
            crearProyectoUseCase = get(),
            actualizarProyectoUseCase = get(),
            eliminarProyectoUseCase = get()
        )
    }
    viewModel { ProyectosViewModel(get(),get()) }

    single<EstadisticaRepository> { EstadisticaRepositoryImpl(get(), get()) }
    factory { ObtenerEstadisticasUseCase(get()) }
    viewModel { EstadisticasViewModel(get()) }

    factory { com.softwama.goplan.features.profile.domain.usecase.UpdateProfileUseCase(get()) }
    factory { com.softwama.goplan.features.profile.domain.usecase.GetThemeUseCase(get()) }
    factory { com.softwama.goplan.features.profile.domain.usecase.SetThemeUseCase(get()) }

    viewModel { com.softwama.goplan.features.profile.presentation.EditProfileViewModel(get(), get()) }
    viewModel { com.softwama.goplan.features.profile.presentation.SettingsViewModel(get(), get()) }

    single<ActividadRepository> { ActividadRepositoryImpl(get(),null,get()) }
    factory { ObtenerActividadesUseCase(get()) }
    factory { CrearActividadUseCase(get()) }
    factory { ActualizarActividadUseCase(get()) }
    factory { EliminarActividadUseCase(get()) }
    factory {
        ActividadUseCases(
            obtenerActividadesUseCase = get(),
            crearActividadUseCase = get(),
            actualizarActividadUseCase = get(),
            eliminarActividadUseCase = get()
        )
    }
}