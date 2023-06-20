package com.example.homeautomation

import android.content.Context
import android.content.SharedPreferences
import com.example.homeautomation.settings.HomeAssistantEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PreferenceManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean("isLoggedIn", false)
    }
    fun setLoggedIn(isLoggedIn: Boolean) {
        sharedPreferences.edit().putBoolean("isLoggedIn", isLoggedIn).apply()
    }

    fun setEndpoint(endpoint: String) {
        sharedPreferences.edit().putString("Endpoint", endpoint).apply()
    }
    fun getEndpoint(): String? {
        return sharedPreferences.getString("Endpoint", null)
    }

    fun setFirstName(name: String) {
        sharedPreferences.edit().putString("FirstName", name).apply()
    }
    fun getFirstName(): String? {
        return sharedPreferences.getString("FirstName", null)
    }

    fun setLastName(name: String) {
        sharedPreferences.edit().putString("LastName", name).apply()
    }
    fun getLastName(): String? {
        return sharedPreferences.getString("LastName", null)
    }

    fun saveEntities(key: String, entities: List<HomeAssistantEntity>) {
        val json = gson.toJson(entities)
        sharedPreferences.edit().putString(key, json).apply()
    }
    fun updateEntity(entity: HomeAssistantEntity) {
        val entities = getEntities("entities")
        val updatedEntities = entities.map {
            if (it.entityId == entity.entityId) entity else it
        }
        saveEntities("entities", updatedEntities)
    }
    fun getEntities(key: String): List<HomeAssistantEntity> {
        val json = sharedPreferences.getString(key, null)
        val type = object : TypeToken<List<HomeAssistantEntity>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }
}
