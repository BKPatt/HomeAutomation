package com.example.homeautomation

import android.content.Context
import android.content.SharedPreferences

class PreferenceManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)

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
}
