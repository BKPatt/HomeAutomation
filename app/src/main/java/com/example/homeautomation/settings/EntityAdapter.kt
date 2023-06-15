package com.example.homeautomation.settings

import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.homeautomation.R
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import java.util.Calendar

class EntityAdapter(private val groupedEntities: List<GroupedEntity>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val ITEM_TYPE_SWITCH = 1
        private const val ITEM_TYPE_DROPDOWN = 2
        private const val ITEM_TYPE_SCROLLABLE_BAR = 3
        private const val ITEM_TYPE_COLOR_PICKER = 4
        private const val ITEM_TYPE_CHECKBOX = 5
        private const val ITEM_TYPE_DATE_PICKER = 6
        private const val ITEM_TYPE_TEXT_INPUT = 7
        private const val ITEM_TYPE_BUTTON = 8
        private const val ITEM_TYPE_GROUP = 9
    }

    inner class GroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val groupNameTextView: TextView = itemView.findViewById(R.id.groupNameTextView)
    }

    inner class EntityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val labelTextView: TextView = itemView.findViewById(R.id.labelTextView)
        // val entityContainer: LinearLayout = itemView.findViewById(R.id.entityContainer)

        init {
            itemView.setOnClickListener {
                val groupedEntity = groupedEntities[adapterPosition]
                val viewType = getItemViewType(adapterPosition)

                groupedEntity.entities.forEach { entity ->
                    showEditableDialog(it.context, viewType, entity)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when {
            viewType == ITEM_TYPE_GROUP -> createGroupViewHolder(parent)
            viewType == ITEM_TYPE_SWITCH -> createEntityViewHolder(parent, R.layout.switch_item)
            viewType == ITEM_TYPE_DROPDOWN -> createEntityViewHolder(parent, R.layout.item_dropdown)
            viewType == ITEM_TYPE_SCROLLABLE_BAR -> createEntityViewHolder(parent, R.layout.scrollable_item)
            viewType == ITEM_TYPE_COLOR_PICKER -> createEntityViewHolder(parent, R.layout.color_picker)
            viewType == ITEM_TYPE_CHECKBOX -> createEntityViewHolder(parent, R.layout.checkbox)
            viewType == ITEM_TYPE_DATE_PICKER -> createEntityViewHolder(parent, R.layout.date_picker)
            viewType == ITEM_TYPE_TEXT_INPUT -> createEntityViewHolder(parent, R.layout.text_input)
            viewType == ITEM_TYPE_BUTTON -> createEntityViewHolder(parent, R.layout.button_item)
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val groupedEntity = groupedEntities[position]

        when(holder) {
            is GroupViewHolder -> {
                holder.groupNameTextView.text = groupedEntity.groupName
            }
            is EntityViewHolder -> {
                groupedEntity.entities.forEach { entity ->
                    holder.labelTextView.text = entity.friendlyName

                    // Handle click events
                    holder.labelTextView.setOnClickListener {
                        showEditableDialog(it.context, getItemViewType(position), entity)
                    }

                    // Bind other views based on the entity type
                    when (getItemViewType(position)) {
                        ITEM_TYPE_SWITCH -> {
                            val entitySwitch: Switch = holder.itemView.findViewById(R.id.entitySwitch)
                            entitySwitch.isChecked = entity.state == "on"
                            entitySwitch.isEnabled = entity.enabled
                        }
                        ITEM_TYPE_DROPDOWN -> {
                            val optionsSpinner: Spinner = holder.itemView.findViewById(R.id.optionsSpinner)
                            optionsSpinner.adapter = ArrayAdapter(
                                holder.itemView.context,
                                android.R.layout.simple_spinner_item,
                                entity.availableModes ?: listOf()
                            )
                            optionsSpinner.isEnabled = entity.enabled
                        }
                        ITEM_TYPE_SCROLLABLE_BAR -> {
                            val seekBar: SeekBar = holder.itemView.findViewById(R.id.seekBar)
                            seekBar.progress = entity.brightness
                            seekBar.isEnabled = entity.enabled
                        }
                        ITEM_TYPE_COLOR_PICKER -> {
                            val colorPickerButton: Button = holder.itemView.findViewById(R.id.colorPickerButton)
                            colorPickerButton.setOnClickListener {
                                showColorPickerDialog(it.context)
                            }
                            colorPickerButton.isEnabled = entity.enabled
                        }
                        ITEM_TYPE_CHECKBOX -> {
                            val checkbox: CheckBox = holder.itemView.findViewById(R.id.checkbox)
                            checkbox.isChecked = entity.state == "on"
                            checkbox.isEnabled = entity.enabled
                        }
                        ITEM_TYPE_DATE_PICKER -> {
                            val datePicker: TextView = holder.itemView.findViewById(R.id.datePicker)
                            datePicker.text = entity.state
                            datePicker.setOnClickListener {
                                showDatePickerDialog(it.context, datePicker)
                            }
                            datePicker.isEnabled = entity.enabled
                        }
                        ITEM_TYPE_TEXT_INPUT -> {
                            val textInput: EditText = holder.itemView.findViewById(R.id.textInput)
                            textInput.setText(entity.state)
                            textInput.isEnabled = entity.enabled
                        }
                        ITEM_TYPE_BUTTON -> {
                            val button: Button = holder.itemView.findViewById(R.id.button)
                            button.setOnClickListener {
                                // Perform button action
                            }
                            button.isEnabled = entity.enabled
                        }
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return groupedEntities.size
    }


    override fun getItemViewType(position: Int): Int {
        val groupedEntity = groupedEntities[position]

        // If the group has no entities, then return ITEM_TYPE_GROUP
        if (groupedEntity.entities.isEmpty()) {
            return ITEM_TYPE_GROUP
        }

        val firstEntity = groupedEntity.entities[0]
        return when (firstEntity.type) {
            "light" -> ITEM_TYPE_SWITCH
            "climate" -> ITEM_TYPE_DROPDOWN
            "brightness" -> ITEM_TYPE_SCROLLABLE_BAR
            "color" -> ITEM_TYPE_COLOR_PICKER
            "checkbox" -> ITEM_TYPE_CHECKBOX
            "date" -> ITEM_TYPE_DATE_PICKER
            "text_input" -> ITEM_TYPE_TEXT_INPUT
            "button" -> ITEM_TYPE_BUTTON
            else -> throw IllegalArgumentException("Invalid entity type")
        }
    }

    private fun createGroupViewHolder(parent: ViewGroup): GroupViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.entity_container, parent, false)
        return GroupViewHolder(view)
    }

    private fun createEntityViewHolder(parent: ViewGroup, layoutId: Int): EntityViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(layoutId, parent, false)
        return EntityViewHolder(view)
    }

    private fun showEditableDialog(context: Context, viewType: Int, entity: HomeAssistantEntity) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_custom, null)
        val field1EditText: EditText = dialogView.findViewById(R.id.field1)
        val field2EditText: EditText = dialogView.findViewById(R.id.field2)

        field1EditText.setText(entity.friendlyName)
        field2EditText.setText("${viewTypeToString(viewType)} - ${entity.type}")

        AlertDialog.Builder(context)
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val field1Input = field1EditText.text.toString()
                val field2Input = field2EditText.text.toString()
                // Save the inputs
            }
            .setNegativeButton("Cancel", null)
            .create()
            .show()
    }

    private fun viewTypeToString(viewType: Int): String {
        return when (viewType) {
            ITEM_TYPE_SWITCH -> "Switch"
            ITEM_TYPE_DROPDOWN -> "Dropdown"
            ITEM_TYPE_SCROLLABLE_BAR -> "Scrollable Bar"
            ITEM_TYPE_COLOR_PICKER -> "Color Picker"
            ITEM_TYPE_CHECKBOX -> "Checkbox"
            ITEM_TYPE_DATE_PICKER -> "Date Picker"
            ITEM_TYPE_TEXT_INPUT -> "Text Input"
            ITEM_TYPE_BUTTON -> "Button"
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    private fun showColorPickerDialog(context: Context) {
        ColorPickerDialog.Builder(context)
            .setTitle("ColorPicker Dialog")
            .setPreferenceName("MyColorPickerDialog")
            .setPositiveButton("Confirm", ColorEnvelopeListener { envelope, _ ->
                // Handle color selection
            })
            .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialogInterface, _ ->
                dialogInterface.dismiss()
            })
            .attachAlphaSlideBar(true)
            .attachBrightnessSlideBar(true)
            .setBottomSpace(12)
            .show()
    }

    private fun showDatePickerDialog(context: Context, textView: TextView) {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                textView.text = "$year-${month + 1}-$dayOfMonth"
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.datePicker.minDate = calendar.timeInMillis
        datePickerDialog.show()
    }
}
