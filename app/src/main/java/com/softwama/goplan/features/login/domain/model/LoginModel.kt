package com.softwama.goplan.features.login.domain.model

data class LoginRequest(
    val username: String,
    val password: String
)

data class LoginResponse(
    val token: String,
    val userId: String,
    val message: String = "Login exitoso"
)