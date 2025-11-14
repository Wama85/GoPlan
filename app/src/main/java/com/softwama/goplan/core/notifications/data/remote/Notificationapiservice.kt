package com.softwama.goplan.core.notifications.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.PUT

interface NotificationApiService {

    @POST("api/fcm/token")
    suspend fun registerFcmToken(
        @Body request: FcmTokenRequest
    ): Response<FcmTokenResponse>

    @PUT("api/fcm/token")
    suspend fun updateFcmToken(
        @Body request: FcmTokenRequest
    ): Response<FcmTokenResponse>

    @DELETE("api/fcm/token")
    suspend fun deleteFcmToken(
        @Body request: DeleteTokenRequest
    ): Response<Unit>
}

data class FcmTokenRequest(
    val token: String,
    val userId: String,
    val platform: String = "android"
)

data class DeleteTokenRequest(
    val token: String
)

data class FcmTokenResponse(
    val success: Boolean,
    val message: String
)