package com.softwama.goplan.core.notifications.domain.model

data class NotificationData(
    val title: String?,
    val body: String?,
    val navigateTo: String? = null,
    val taskId: String? = null,
    val eventId: String? = null,
    val type: String? = null,
    val extras: Map<String, String> = emptyMap()
)