package com.example.homeautomation.settings

data class HomeAssistantEntity(
    val entityId: String,
    val state: String,
    val friendlyName: String,
    val brightness: Int,
    val colorTemp: Int,
    val temperature: Double,
    val currentMode: String,
    val availableModes: List<String>,
    val type: String,
    val clickable: Boolean,
    val enabled: Boolean
)