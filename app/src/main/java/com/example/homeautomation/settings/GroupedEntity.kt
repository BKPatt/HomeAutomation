package com.example.homeautomation.settings

data class GroupedEntity(
    val groupName: String,
    val entities: List<HomeAssistantEntity>
)