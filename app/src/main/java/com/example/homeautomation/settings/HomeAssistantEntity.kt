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
    val groupName: String,
    val clickable: Boolean,
    val enabled: Boolean
)

sealed class RecyclerViewItem

data class Component(val entity: HomeAssistantEntity) : RecyclerViewItem()

data class GroupTitle(val title: String) : RecyclerViewItem() {
    val groupName: String = title
}
