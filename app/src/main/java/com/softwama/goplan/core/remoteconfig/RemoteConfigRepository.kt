package com.softwama.goplan.core.remoteconfig

import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import kotlinx.coroutines.tasks.await

class RemoteConfigRepository {

    private val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig

    suspend fun fetchMaintenanceStatus(): Boolean {
        remoteConfig.fetchAndActivate().await()
        return remoteConfig.getBoolean("maintenance_mode")
    }
}
