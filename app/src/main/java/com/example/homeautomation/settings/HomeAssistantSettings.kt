package com.example.homeautomation.settings

import android.os.Bundle
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import org.json.JSONArray
import java.io.IOException

class HomeAssistantSettings : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Fetch all entities state from Home Assistant
        val instanceUrl = "Your Home Assistant URL here"
        val accessToken = "Your Access Token here"
        fetchAllEntitiesState(instanceUrl, accessToken)
    }

    private fun fetchAllEntitiesState(instanceUrl: String, accessToken: String) {
        val client = OkHttpClient()

        val request = Request.Builder()
            .url("$instanceUrl/api/states")
            .addHeader("Authorization", "Bearer $accessToken")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                val jsonArray = JSONArray(body)

                val sharedPref = getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
                val editor = sharedPref.edit()

                // Iterate over all entities
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)

                    val entityId = jsonObject.getString("entity_id")
                    val state = jsonObject.getString("state")

                    // Store the state of each entity
                    editor.putString("entity_${entityId}_state", state)
                }

                editor.apply()
            }

            override fun onFailure(call: Call, e: IOException) {
                // Handle the error
            }
        })
    }
}