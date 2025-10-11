package com.softwama.goplan.features.login.domain.model

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val token: String? = null,
    val userName: String? = null,
    val userEmail: String? = null
)