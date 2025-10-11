package com.softwama.goplan.features.calendar.domain.model

data class CalendarEvent(
    val id: String,
    val title: String,
    val description: String,
    val startTime: String,  // Formato: "2025-10-15T10:00:00"
    val endTime: String,
    val location: String? = null,
    val color: EventColor = EventColor.BLUE
)

enum class EventColor {
    BLUE,
    RED,
    GREEN,
    ORANGE,
    PURPLE
}