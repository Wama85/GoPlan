// di/TareasModule.kt
package com.softwama.goplan.di

import com.softwama.goplan.features.tareas.data.repository.TareaRepositoryImpl
import com.softwama.goplan.features.tareas.domain.repository.TareaRepository
import com.softwama.goplan.features.tareas.domain.usecase.*
import com.softwama.goplan.features.tareas.presentation.TareasViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val tareasModule = module {
    single<TareaRepository> { TareaRepositoryImpl() }

    single { ObtenerTareasUseCase(get()) }
    single { CrearTareaUseCase(get()) }
    single { ActualizarTareaUseCase(get()) }
    single { EliminarTareaUseCase(get()) }

    single {
        TareaUseCases(
            obtenerTareasUseCase = get(),
            crearTareaUseCase = get(),
            actualizarTareaUseCase = get(),
            eliminarTareaUseCase = get()
        )
    }

    viewModel { TareasViewModel(get()) }
}