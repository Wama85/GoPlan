package com.softwama.goplan.core.notifications.data.datasource

import com.softwama.goplan.core.notifications.data.remote.DeleteTokenRequest
import com.softwama.goplan.core.notifications.data.remote.FcmTokenRequest
import com.softwama.goplan.core.notifications.data.remote.NotificationApiService

interface NotificationRemoteDataSource {
    suspend fun sendTokenToBackend(token: String, userId: String)
    suspend fun deleteToken(token: String)
}

class NotificationRemoteDataSourceImpl(
    private val apiService: NotificationApiService
) : NotificationRemoteDataSource {

    override suspend fun sendTokenToBackend(token: String, userId: String) {
        val request = FcmTokenRequest(
            token = token,
            userId = userId,
            platform = "android"
        )

        val response = apiService.updateFcmToken(request)
        if (!response.isSuccessful) {
            throw Exception("Error enviando token: ${response.message()}")
        }
    }

    override suspend fun deleteToken(token: String) {
        val request = DeleteTokenRequest(token = token)
        val response = apiService.deleteFcmToken(request)

        if (!response.isSuccessful) {
            throw Exception("Error eliminando token: ${response.message()}")
        }
    }
}