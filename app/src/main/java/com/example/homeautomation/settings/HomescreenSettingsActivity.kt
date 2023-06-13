package com.example.homeautomation.settings

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.homeautomation.R
import org.json.JSONArray

class HomescreenSettingsActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var backButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        backButton = findViewById(R.id.back)

        // Example entities from Home Assistant API response
        val apiResponse = """
            [
                {
                    "entity_id": "light.living_room",
                    "state": "on",
                    "attributes": {
                        "friendly_name": "Living Room Light",
                        "brightness": 180,
                        "color_temp": 350
                    }
                },
                {
                    "entity_id": "climate.bedroom",
                    "state": "cool",
                    "attributes": {
                        "friendly_name": "Bedroom Climate",
                        "temperature": 22.5,
                        "current_mode": "cool",
                        "available_modes": ["cool", "heat", "auto"]
                    }
                },
                {
                    "entity_id": "brightness.living_room",
                    "state": "50",
                    "attributes": {
                        "friendly_name": "Living Room Brightness",
                        "brightness": 50
                    }
                },
                {
                    "entity_id": "color.living_room",
                    "state": "red",
                    "attributes": {
                        "friendly_name": "Living Room Color",
                        "color": "red"
                    }
                },
                {
                    "entity_id": "checkbox.kitchen",
                    "state": "on",
                    "attributes": {
                        "friendly_name": "Kitchen Checkbox"
                    }
                },
                {
                    "entity_id": "date.living_room",
                    "state": "2023-06-13",
                    "attributes": {
                        "friendly_name": "Living Room Date"
                    }
                },
                {
                    "entity_id": "text_input.bedroom",
                    "state": "Hello, World!",
                    "attributes": {
                        "friendly_name": "Bedroom Text Input"
                    }
                },
                {
                    "entity_id": "button.trigger_automation",
                    "state": "off",
                    "attributes": {
                        "friendly_name": "Trigger Automation"
                    }
                }
            ]
        """.trimIndent()

        // Parse the JSON response and create entities
        val entities = parseEntitiesFromApiResponse(apiResponse)

        // Update the RecyclerView with the entities
        recyclerView.adapter = EntityAdapter(entities)

        backButton.setOnClickListener {
            finish()
        }
    }

    // Function to parse entities from the API response
    private fun parseEntitiesFromApiResponse(apiResponse: String): List<HomeAssistantEntity> {
        val entities = mutableListOf<HomeAssistantEntity>()

        val jsonArray = JSONArray(apiResponse)
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)

            val entityId = jsonObject.getString("entity_id")
            val type = entityId.split('.')[0]  // Use the first part of entity_id as type
            val state = jsonObject.getString("state")
            val attributes = jsonObject.optJSONObject("attributes")

            val friendlyName = attributes?.getString("friendly_name") ?: ""
            val brightness = attributes?.optInt("brightness") ?: 0
            val colorTemp = attributes?.optInt("color_temp") ?: 0
            val temperature = attributes?.optDouble("temperature") ?: 0.0
            val currentMode = attributes?.optString("current_mode") ?: ""
            val availableModes = attributes?.optJSONArray("available_modes")?.let { jsonArrayToList(it) }

            val entity = HomeAssistantEntity(
                entityId,
                state,
                friendlyName,
                brightness,
                colorTemp,
                temperature,
                currentMode,
                availableModes ?: listOf(),
                type
            )

            entities.add(entity)
        }

        return entities
    }



    private fun jsonArrayToList(jsonArray: JSONArray): List<String> {
        val list = mutableListOf<String>()
        for (i in 0 until jsonArray.length()) {
            val value = jsonArray.getString(i)
            list.add(value)
        }
        return list
    }
}

