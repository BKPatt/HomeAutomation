package com.example.homeautomation.settings

import android.app.DatePickerDialog
import android.content.DialogInterface
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
            (view as? TextView)?.text = entity.friendlyName
        }

        when (viewType) {
            ITEM_TYPE_SWITCH -> {
                generalHolder.views[R.id.entitySwitch]?.let { view ->
                    (view as? Switch)?.isChecked = entity.state == "on"
                }
            }
            ITEM_TYPE_DROPDOWN -> {
                generalHolder.views[R.id.optionsSpinner]?.let { view ->
                    val adapter = ArrayAdapter(
                        view.context,
                        android.R.layout.simple_spinner_item,
                        entity.availableModes ?: listOf()
                    )
                    (view as? Spinner)?.adapter = adapter
                }
            }
            ITEM_TYPE_SCROLLABLE_BAR -> {
                generalHolder.views[R.id.seekBar]?.let { view ->
                    (view as? SeekBar)?.progress = entity.brightness
                }
            }
            ITEM_TYPE_COLOR_PICKER -> {
                generalHolder.views[R.id.colorPickerButton]?.let { view ->
                    (view as? Button)?.setOnClickListener {
                        ColorPickerDialog.Builder(view.context)
                            .setTitle("ColorPicker Dialog")
                            .setPreferenceName("MyColorPickerDialog")
                            .setPositiveButton(
                                "Confirm",
                                ColorEnvelopeListener { envelope, fromUser ->
                                    // do something with envelope.color here
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
                }
            }
            ITEM_TYPE_CHECKBOX -> {
                generalHolder.views[R.id.checkbox]?.let { view ->
                    (view as? CheckBox)?.isChecked = entity.state == "on"
                }
            }
            ITEM_TYPE_DATE_PICKER -> {
                generalHolder.views[R.id.datePicker]?.let { view ->
                    val textView = view as? TextView
                    textView?.text = entity.state
                    textView?.setOnClickListener {
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
                }
            }
            ITEM_TYPE_TEXT_INPUT -> {
                generalHolder.views[R.id.textInput]?.let { view ->
                    (view as? EditText)?.setText(entity.state)
                }
            }
            ITEM_TYPE_BUTTON -> {
                generalHolder.views[R.id.button]?.let { view ->
                    (view as? Button)?.setOnClickListener {
                        // Do something on button click
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
