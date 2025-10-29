
package com.softwama.goplan.features.suscribe.domain.usecase

import com.softwama.goplan.features.suscribe.domain.model.Suscribe
import com.softwama.goplan.features.suscribe.domain.repository.SuscribeRepository

class RegistrarUsuarioUseCase(
    private val repository: SuscribeRepository
) {
    suspend operator fun invoke(suscribe: Suscribe): Result<String> {
        // Validaciones
        if (suscribe.nombre.isBlank()) {
            return Result.failure(Exception("El nombre es requerido"))
        }
        if (suscribe.apellido.isBlank()) {
            return Result.failure(Exception("El apellido es requerido"))
        }
        if (!suscribe.correo.contains("@")) {
            return Result.failure(Exception("Correo inválido"))
        }
        if (suscribe.fechaNac.isBlank()) {
            return Result.failure(Exception("La fecha de nacimiento es requerida"))
        }

        if (suscribe.pass.length < 6) {
            return Result.failure(Exception("La contraseña debe tener al menos 6 caracteres"))
        }
        if (suscribe.pass != suscribe.repitPass) {
            return Result.failure(Exception("Las contraseñas no coinciden"))
        }

        return repository.registrarUsuario(suscribe)
    }
}