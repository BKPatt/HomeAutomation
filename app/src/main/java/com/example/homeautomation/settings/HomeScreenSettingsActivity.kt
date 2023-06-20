package com.example.homeautomation.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.homeautomation.PreferenceManager
import com.example.homeautomation.R
import org.json.JSONArray

class HomeScreenSettingsActivity : AppCompatActivity(), AddPopupListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var backButton: Button
    private lateinit var editButton: Button
    private lateinit var backAfterEdit: Button
    private lateinit var addButton: Button
    private lateinit var buttonContainer: LinearLayout
    private lateinit var recyclerViewAdapter: EntityAdapter
    private lateinit var updatedItems: List<RecyclerViewItem>
    private val entities = mutableListOf<HomeAssistantEntity>()

    private lateinit var preferenceManager: PreferenceManager
    private lateinit var apiResponse: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings)

        preferenceManager = PreferenceManager(this)

        recyclerView = findViewById(R.id.inputRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        backButton = findViewById(R.id.back)
        editButton = findViewById(R.id.edit)
        backAfterEdit = findViewById(R.id.backAfterEdit)
        addButton = findViewById(R.id.add)
        buttonContainer = findViewById(R.id.buttonsContainer)

        backButton.setOnClickListener {
            finish()
        }

        editButton.setOnClickListener {
            backButton.visibility = View.GONE
            editButton.visibility = View.GONE
            backAfterEdit.visibility = View.VISIBLE
            addButton.visibility = View.VISIBLE
            val updatedEntities = entities.map { entity ->
                entity.copy(clickable = true, enabled = false)
            }
            updateEntities(updatedEntities)
        }

        backAfterEdit.setOnClickListener {
            backButton.visibility = View.VISIBLE
            editButton.visibility = View.VISIBLE
            backAfterEdit.visibility = View.GONE
            addButton.visibility = View.GONE
            val updatedEntities = entities.map { entity ->
                entity.copy(clickable = false, enabled = true)
            }
            updateEntities(updatedEntities)
        }

        addButton.setOnClickListener {
            showAddDialog()
        }

        createEntities()
        preferenceManager = PreferenceManager(this)
        updatedItems = createRecyclerViewItems(entities)
        recyclerViewAdapter = createAdapter(updatedItems, preferenceManager, entities)
        recyclerView.adapter = recyclerViewAdapter
    }

    override fun onSaveClicked(entityId: String, state: String, friendlyName: String, attributes: Map<String, Any>) {
        val entity = HomeAssistantEntity(
            entityId = entityId,
            state = state,
            friendlyName = friendlyName,
            brightness = attributes["brightness"] as? Int ?: 0,
            colorTemp = attributes["color_temp"] as? Int ?: 0,
            temperature = attributes["temperature"] as? Double,
            currentMode = attributes["current_mode"] as? String ?: "",
            availableModes = attributes["available_modes"] as? List<String> ?: emptyList(),
            type = entityId.split('.')[0],
            groupName = entityId.split('.')[1],
            clickable = false,
            enabled = true,
            attributes = attributes
        )

        entities.add(entity)
        preferenceManager.saveEntities("entities", entities)

        val updatedItems = createRecyclerViewItems(entities)
        recyclerViewAdapter.updateItems(updatedItems)

        buttonContainer.visibility = View.VISIBLE
    }

    override fun onCancelClicked() {
        buttonContainer.visibility = View.VISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()

        val updatedEntities = entities.map { entity ->
            entity.copy(clickable = false, enabled = true)
        }
        preferenceManager.saveEntities("entities", updatedEntities)
    }

    private fun showAddDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.popup_add, null)
        val friendlyNameEditText = dialogView.findViewById<EditText>(R.id.editTextFriendlyName)
        val stateEditText = dialogView.findViewById<EditText>(R.id.editTextState)
        val entityIdEditText = dialogView.findViewById<EditText>(R.id.editTextEntityId)
        val enableStateButton = dialogView.findViewById<Button>(R.id.enableState)

        val entityTypes = arrayOf(
            "light", "climate", "brightness", "color",
            "checkbox", "date", "text_input", "button"
        )

        val entityTypeSpinner = dialogView.findViewById<Spinner>(R.id.spinnerEntityType)
        val entityTypeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, entityTypes)
        entityTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        entityTypeSpinner.adapter = entityTypeAdapter

        val alertDialog = AlertDialog.Builder(this)
            .setTitle("Add Object")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val friendlyNameInput = friendlyNameEditText.text.toString()
                val stateInput = stateEditText.text.toString()
                val entityTypeInput = entityTypeSpinner.selectedItem.toString()
                val entityIdInput = "$entityTypeInput.${entityIdEditText.text}"

                if (friendlyNameInput.isNotEmpty() && stateInput.isNotEmpty() && entityIdInput.isNotEmpty()) {
                    val newEntity = HomeAssistantEntity(
                        entityId = entityIdInput,
                        state = stateInput,
                        friendlyName = friendlyNameInput,
                        brightness = 0,
                        colorTemp = 0,
                        temperature = null,
                        currentMode = "",
                        availableModes = emptyList(),
                        type = entityTypeInput,
                        groupName = "",
                        clickable = false,
                        enabled = true,
                        attributes = emptyMap()
                    )

                    entities.add(newEntity)
                    preferenceManager.saveEntities("entities", entities)

                    val updatedItems = createRecyclerViewItems(entities)
                    recyclerViewAdapter.updateItems(updatedItems)
                }
            }
            .setNegativeButton("Cancel", null)
            .create()

        enableStateButton.setOnClickListener {
            stateEditText.isEnabled = true
            enableStateButton.visibility = View.GONE
        }

        alertDialog.setOnDismissListener {
            stateEditText.isEnabled = false
            enableStateButton.visibility = View.VISIBLE
        }

        alertDialog.show()
    }

    private fun createEntities() {
        apiResponse = applicationContext.resources.openRawResource(R.raw.mock_data).bufferedReader().use { it.readText() }

        val savedEntities = preferenceManager.getEntities("entities")
        if (savedEntities.isNotEmpty()) {
            entities.addAll(savedEntities.map { entity ->
                entity.copy(clickable = false, enabled = true)
            })
            updatedItems = createRecyclerViewItems(entities)
        } else {
            val apiResponseEntities = parseEntitiesFromApiResponse(apiResponse)
            entities.addAll(apiResponseEntities.map { entity ->
                entity.copy(clickable = false, enabled = true)
            })
            updatedItems = createRecyclerViewItems(apiResponseEntities)
            preferenceManager.saveEntities("entities", apiResponseEntities)
        }
    }

    private fun createAdapter(entities: List<RecyclerViewItem>, preferenceManager: PreferenceManager, entitiesList: MutableList<HomeAssistantEntity>): EntityAdapter {
        return EntityAdapter(this, entities, preferenceManager, entitiesList)
    }

    private fun updateEntities(updatedEntities: List<HomeAssistantEntity>) {
        entities.clear()
        entities.addAll(updatedEntities)

        preferenceManager.saveEntities("entities", updatedEntities)

        updatedItems = createRecyclerViewItems(updatedEntities)
        recyclerViewAdapter.updateItems(updatedItems)
    }

    private fun createRecyclerViewItems(entities: List<HomeAssistantEntity>): List<RecyclerViewItem> {
        val recyclerViewItems = mutableListOf<RecyclerViewItem>()
        val groupedEntities = entities.groupBy { it.groupName }

        for ((groupName, entities) in groupedEntities) {
            val formattedGroupName = groupName.split("_")
                .joinToString(" ") { it.replaceFirstChar { char -> if (char.isLowerCase()) char.titlecase() else char.toString() } }

            recyclerViewItems.add(GroupTitle(formattedGroupName))
            recyclerViewItems.addAll(entities.map { Component(it) })
        }

        return recyclerViewItems
    }

    private fun parseDoubleOrNull(value: String?): Double? {
        return try {
            if (value != null && value != "NaN") value.toDouble() else null
        } catch (e: NumberFormatException) {
            null
        }
    }

    private fun parseEntitiesFromApiResponse(apiResponse: String): List<HomeAssistantEntity> {
        val entities = mutableListOf<HomeAssistantEntity>()

        val jsonArray = JSONArray(apiResponse)
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)

            val entityId = jsonObject.getString("entity_id")
            val state = jsonObject.getString("state")
            val attributes = jsonObject.optJSONObject("attributes")
            val groupName = entityId.split(".")[1].replace("_", " ").split(" ").joinToString(" ") { word ->
                word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
            }

            val friendlyName = attributes?.getString("friendly_name") ?: ""
            val brightness = attributes?.optInt("brightness") ?: 0
            val colorTemp = attributes?.optInt("color_temp") ?: 0
            val temperature = parseDoubleOrNull(attributes?.optString("temperature"))
            val currentMode = attributes?.optString("current_mode") ?: ""
            val availableModes = attributes?.optJSONArray("available_modes")?.let { jsonArrayToList(it) }

            val attributesMap = mutableMapOf<String, Any>()
            if (attributes != null) {
                val attributesKeys = attributes.keys()
                while (attributesKeys.hasNext()) {
                    val key = attributesKeys.next() as String
                    val value = attributes.get(key)
                    attributesMap[key] = value
                }
            }

            val entity = HomeAssistantEntity(
                entityId = entityId,
                state = state,
                friendlyName = friendlyName,
                brightness = brightness,
                colorTemp = colorTemp,
                temperature = temperature,
                currentMode = currentMode,
                availableModes = availableModes ?: listOf(),
                type = entityId.split('.')[0],
                groupName = groupName,
                clickable = false,
                enabled = true,
                attributes = attributesMap
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
