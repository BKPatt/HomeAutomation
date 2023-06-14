package com.example.homeautomation.settings

import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
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

class EntityAdapter(private val entities: List<HomeAssistantEntity>) :
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
    }

    inner class GeneralViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val views: MutableMap<Int, View> = mutableMapOf()

        init {
            views[R.id.labelTextView] = itemView.findViewById(R.id.labelTextView)

            val ids = listOf(
                R.id.entitySwitch, R.id.optionsSpinner, R.id.seekBar,
                R.id.colorPickerButton, R.id.checkbox, R.id.datePicker,
                R.id.textInput, R.id.button
            )
            for (id in ids) {
                itemView.findViewById<View>(id)?.let { views[id] = it }
            }
        }
    }

    private fun createGeneralViewHolder(parent: ViewGroup, layoutId: Int): GeneralViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(layoutId, parent, false)
        return GeneralViewHolder(view)
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

    fun showEditableDialog(context: Context, viewType: Int, entity: HomeAssistantEntity) {
        // create and set layout of the dialog
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_custom, null)
        val field1EditText = dialogView.findViewById<EditText>(R.id.field1)
        // TODO: change field 2 to dropdown instead of text input
        val field2EditText = dialogView.findViewById<EditText>(R.id.field2)

        // set initial text
        field1EditText.setText(entity.friendlyName)

        // Set the input type for the second field based on the entity
        field2EditText.setText(viewTypeToString(viewType))

        AlertDialog.Builder(context)
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                // save the inputs when the positive button is clicked
                val field1Input = field1EditText.text.toString()
                val field2Input = field2EditText.text.toString()

                // TODO: Save the input from user
            }
            .setNegativeButton("Cancel", null)
            .create()
            .show()
    }

    private fun scaleDrawable(context: Context, drawableId: Int, width: Int, height: Int): Drawable {
        val bitmap = BitmapFactory.decodeResource(context.resources, drawableId)
        val bitmapScaled = Bitmap.createScaledBitmap(bitmap, width, height, true)
        return BitmapDrawable(context.resources, bitmapScaled)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GeneralViewHolder {
        return when (viewType) {
            ITEM_TYPE_SWITCH -> createGeneralViewHolder(parent, R.layout.switch_item)
            ITEM_TYPE_DROPDOWN -> createGeneralViewHolder(parent, R.layout.item_dropdown)
            ITEM_TYPE_SCROLLABLE_BAR -> createGeneralViewHolder(parent, R.layout.scrollable_item)
            ITEM_TYPE_COLOR_PICKER -> createGeneralViewHolder(parent, R.layout.color_picker)
            ITEM_TYPE_CHECKBOX -> createGeneralViewHolder(parent, R.layout.checkbox)
            ITEM_TYPE_DATE_PICKER -> createGeneralViewHolder(parent, R.layout.date_picker)
            ITEM_TYPE_TEXT_INPUT -> createGeneralViewHolder(parent, R.layout.text_input)
            ITEM_TYPE_BUTTON -> createGeneralViewHolder(parent, R.layout.button_item)
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val entity = entities[position]
        val viewType = getItemViewType(position)
        val generalHolder = holder as GeneralViewHolder

        generalHolder.views[R.id.labelTextView]?.let { view ->
            (view as? TextView)?.apply {
                text = entity.friendlyName
                isClickable = entity.clickable
                if (entity.clickable) {
                    val editDrawable = scaleDrawable(context, R.drawable.edit, 30, 30)
                    setCompoundDrawablesWithIntrinsicBounds(null, null, editDrawable, null)
                    compoundDrawablePadding = 10
                    setOnClickListener {
                        showEditableDialog(context, viewType, entity)
                    }
                } else {
                    setOnClickListener(null)
                    setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                }
            }
        }

        when (viewType) {
            ITEM_TYPE_SWITCH -> {
                generalHolder.views[R.id.entitySwitch]?.let { view ->
                    (view as? Switch)?.apply {
                        isChecked = entity.state == "on"
                        isEnabled = entity.enabled
                    }
                }
            }
            ITEM_TYPE_DROPDOWN -> {
                generalHolder.views[R.id.optionsSpinner]?.let { view ->
                    (view as? Spinner)?.apply {
                        adapter = ArrayAdapter(
                            context,
                            android.R.layout.simple_spinner_item,
                            entity.availableModes ?: listOf()
                        )
                        isEnabled = entity.enabled
                    }
                }
            }
            ITEM_TYPE_SCROLLABLE_BAR -> {
                generalHolder.views[R.id.seekBar]?.let { view ->
                    (view as? SeekBar)?.apply {
                        progress = entity.brightness
                        isEnabled = entity.enabled
                    }
                }
            }
            ITEM_TYPE_COLOR_PICKER -> {
                generalHolder.views[R.id.colorPickerButton]?.let { view ->
                    (view as? Button)?.apply {
                        setOnClickListener {
                            ColorPickerDialog.Builder(view.context)
                                .setTitle("ColorPicker Dialog")
                                .setPreferenceName("MyColorPickerDialog")
                                .setPositiveButton(
                                    "Confirm",
                                    ColorEnvelopeListener { envelope, fromUser ->
                                        // TODO: something with envelope.color here
                                    })
                                .setNegativeButton(
                                    "Cancel",
                                    DialogInterface.OnClickListener { dialogInterface, _ ->
                                        dialogInterface.dismiss()
                                    })
                                .attachAlphaSlideBar(true) // default value is true
                                .attachBrightnessSlideBar(true) // default value is true
                                .setBottomSpace(12) // set bottom space between last slidebar and buttons
                                .show()
                        }
                        isEnabled = entity.enabled
                    }
                }
            }
            ITEM_TYPE_CHECKBOX -> {
                generalHolder.views[R.id.checkbox]?.let { view ->
                    (view as? CheckBox)?.apply {
                        isChecked = entity.state == "on"
                        isEnabled = entity.enabled
                    }
                }
            }
            ITEM_TYPE_DATE_PICKER -> {
                generalHolder.views[R.id.datePicker]?.let { view ->
                    val textView = view as? TextView
                    textView?.apply {
                        text = entity.state
                        setOnClickListener {
                            val calendar = Calendar.getInstance()
                            val datePickerDialog = DatePickerDialog(
                                it.context,
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
                        isEnabled = entity.enabled
                    }
                }
            }
            ITEM_TYPE_TEXT_INPUT -> {
                generalHolder.views[R.id.textInput]?.let { view ->
                    (view as? EditText)?.apply {
                        setText(entity.state)
                        isEnabled = entity.enabled
                    }
                }
            }
            ITEM_TYPE_BUTTON -> {
                generalHolder.views[R.id.button]?.let { view ->
                    (view as? Button)?.apply {
                        setOnClickListener {
                            // TODO: Add call
                        }
                        isEnabled = entity.enabled
                    }
                }
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }




    override fun getItemCount(): Int {
        return entities.size
    }

    override fun getItemViewType(position: Int): Int {
        val entity = entities[position]
        return when (entity.type) {
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
}