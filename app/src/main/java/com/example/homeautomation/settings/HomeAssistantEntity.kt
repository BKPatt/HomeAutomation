package com.example.homeautomation.settings

import com.example.homeautomation.PreferenceManager

data class HomeAssistantEntity(
    val entityId: String,
    var state: String,
    val friendlyName: String,
    var brightness: Int,
    var colorTemp: Int,
    var temperature: Double?,
    var currentMode: String,
    var availableModes: List<String>,
    val type: String,
    val groupName: String,
    var clickable: Boolean,
    var enabled: Boolean,
    var attributes: Map<String, Any>? = null // Make attributes nullable
) {
    fun notifyEntityChanged(preferenceManager: PreferenceManager) {
        preferenceManager.updateEntity(this)
    }
}

sealed class RecyclerViewItem

data class Component(val entity: HomeAssistantEntity) : RecyclerViewItem()

data class GroupTitle(val title: String) : RecyclerViewItem() {
    val groupName: String = title
}
