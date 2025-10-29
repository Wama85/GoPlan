package com.softwama.goplan.features.login.domain.repository

interface LoginRepository {
    suspend fun login(email: String, password: String): Result<String>
}