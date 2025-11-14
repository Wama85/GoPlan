package com.softwama.goplan.core.remoteconfig

import com.google.firebase.Firebase

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfig

import kotlinx.coroutines.tasks.await

class RemoteConfigRepository {

    private val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig

    suspend fun fetchMaintenanceStatus(): Boolean {
        remoteConfig.fetchAndActivate().await()
        return remoteConfig.getBoolean("maintenance_mode")
    }
}
