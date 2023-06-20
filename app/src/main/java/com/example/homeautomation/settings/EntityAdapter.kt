package com.example.homeautomation.settings

import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.homeautomation.PreferenceManager
import com.example.homeautomation.R
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import org.json.JSONException
import org.json.JSONObject
import java.util.Calendar

class EntityAdapter(
    private val context: Context,
    private var items: List<RecyclerViewItem>,
    private val preferenceManager: PreferenceManager,
    private var entities: MutableList<HomeAssistantEntity>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var selectedColor: Int? = null

    companion object {
        private const val ITEM_TYPE_SWITCH = 1
        private const val ITEM_TYPE_DROPDOWN = 2
        private const val ITEM_TYPE_SCROLLABLE_BAR = 3
        private const val ITEM_TYPE_COLOR_PICKER = 4
        private const val ITEM_TYPE_CHECKBOX = 5
        private const val ITEM_TYPE_DATE_PICKER = 6
        private const val ITEM_TYPE_TEXT_INPUT = 7
        private const val ITEM_TYPE_BUTTON = 8
        private const val ITEM_TYPE_GROUP_TITLE = 9
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

            itemView.setOnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                    applyBackgroundColor(selectedColor ?: 0)
                    true
                } else {
                    false
                }
            }
        }

        fun applyBackgroundColor(color: Int) {
            views[R.id.colorPickerButton]?.let { view ->
                (view as? Button)?.apply {
                    val drawable = ContextCompat.getDrawable(context, R.drawable.rounded_button)
                    background = drawable
                    drawable?.colorFilter = BlendModeColorFilter(color, BlendMode.SRC_IN)
                }
            }
        }
    }

    inner class GroupTitleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
    }

    private fun createGeneralViewHolder(parent: ViewGroup, layoutId: Int): GeneralViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(layoutId, parent, false)
        return GeneralViewHolder(view)
    }

    private fun createGroupTitleViewHolder(parent: ViewGroup): GroupTitleViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_group_title, parent, false)
        return GroupTitleViewHolder(view)
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
            ITEM_TYPE_GROUP_TITLE -> "Group Title"
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    fun updateItems(items: List<RecyclerViewItem>) {
        this.items = items
        notifyDataSetChanged()
    }

    private fun showEditableDialog(
        context: Context,
        viewType: Int,
        entity: HomeAssistantEntity,
        adapter: EntityAdapter,
        adapterPosition: Int
    ) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.popup_add, null)
        val friendlyNameEditText = dialogView.findViewById<EditText>(R.id.editTextFriendlyName)
        val stateEditText = dialogView.findViewById<EditText>(R.id.editTextState)
        val entityIdEditText = dialogView.findViewById<EditText>(R.id.editTextEntityId)
        val enableStateButton = dialogView.findViewById<Button>(R.id.enableState)

        // Set initial values from the entity object
        friendlyNameEditText.setText(entity.friendlyName)
        stateEditText.setText(entity.state)
        entityIdEditText.setText(entity.entityId)

        val entityTypes = arrayOf(
            "light", "climate", "brightness", "color",
            "checkbox", "date", "text_input", "button"
        )

        val entityTypeSpinner = dialogView.findViewById<Spinner>(R.id.spinnerEntityType)
        val entityTypeAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, entityTypes)
        entityTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        entityTypeSpinner.adapter = entityTypeAdapter

        // Set the selected item based on the entity type of the provided entity
        val entityTypeIndex = entityTypes.indexOf(entity.type)
        if (entityTypeIndex != -1) {
            entityTypeSpinner.setSelection(entityTypeIndex)
        }

        val attributesEditText = dialogView.findViewById<EditText>(R.id.editTextAttributes)
        attributesEditText.setText(entity.attributes?.let { parseAttributesToJson(it) })

        val alertDialog = AlertDialog.Builder(context)
            .setTitle("Edit Object")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val friendlyNameInput = friendlyNameEditText.text.toString()
                val stateInput = stateEditText.text.toString()
                val entityTypeInput = entityTypeSpinner.selectedItem.toString()
                val entityIdInput = entityIdEditText.text.toString()
                val attributesInput = parseAttributes(attributesEditText.text.toString())

                if (friendlyNameInput.isNotEmpty() && stateInput.isNotEmpty() && entityIdInput.isNotEmpty() && attributesInput != null) {
                    val updatedEntity = entity.copy(
                        entityId = entityIdInput,
                        friendlyName = friendlyNameInput,
                        state = stateInput,
                        type = entityTypeInput,
                        attributes = attributesInput
                    )

                    adapter.updateEntity(updatedEntity, adapterPosition)
                    preferenceManager.saveEntities("entities", adapter.entities)
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

    fun updateEntity(entity: HomeAssistantEntity, position: Int) {
        entities[position] = entity
        notifyItemChanged(position)
    }

    private fun parseAttributesToJson(attributes: Map<String, Any>): String {
        val attributesJson = JSONObject(attributes)
        return attributesJson.toString()
    }

    private fun parseAttributes(attributesString: String): Map<String, Any>? {
        if (attributesString.isBlank()) {
            return emptyMap()
        }

        return try {
            val attributesJson = JSONObject(attributesString)
            val keys = attributesJson.keys()
            val attributes = mutableMapOf<String, Any>()
            while (keys.hasNext()) {
                val key = keys.next()
                attributes[key] = attributesJson.get(key)
            }
            attributes
        } catch (e: JSONException) {
            null
        }
    }

    private fun scaleDrawable(context: Context, drawableId: Int, width: Int, height: Int): Drawable {
        val bitmap = BitmapFactory.decodeResource(context.resources, drawableId)
        val bitmapScaled = Bitmap.createScaledBitmap(bitmap, width, height, true)
        return BitmapDrawable(context.resources, bitmapScaled)
    }

    private fun updateButtonColors(button: CompoundButton, isChecked: Boolean, isEnabled: Boolean) {
        button.apply {
            this.isChecked = isChecked
            this.isEnabled = isEnabled
            if (!isEnabled) {
                val color = Color.GRAY
                when (this) {
                    is CheckBox -> buttonTintList = ColorStateList.valueOf(color)
                    is Switch -> {
                        thumbTintList = ColorStateList.valueOf(color)
                        trackTintList = ColorStateList.valueOf(color)
                    }
                }
            } else {
                val colorStateList = ContextCompat.getColorStateList(context, R.color.state)
                when (this) {
                    is CheckBox -> buttonTintList = colorStateList
                    is Switch -> {
                        thumbTintList = colorStateList
                        trackTintList = colorStateList
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_TYPE_SWITCH -> createGeneralViewHolder(parent, R.layout.switch_item)
            ITEM_TYPE_DROPDOWN -> createGeneralViewHolder(parent, R.layout.item_dropdown)
            ITEM_TYPE_SCROLLABLE_BAR -> createGeneralViewHolder(parent, R.layout.scrollable_item)
            ITEM_TYPE_COLOR_PICKER -> createGeneralViewHolder(parent, R.layout.color_picker)
            ITEM_TYPE_CHECKBOX -> createGeneralViewHolder(parent, R.layout.checkbox)
            ITEM_TYPE_DATE_PICKER -> createGeneralViewHolder(parent, R.layout.date_picker)
            ITEM_TYPE_TEXT_INPUT -> createGeneralViewHolder(parent, R.layout.text_input)
            ITEM_TYPE_BUTTON -> createGeneralViewHolder(parent, R.layout.button_item)
            ITEM_TYPE_GROUP_TITLE -> createGroupTitleViewHolder(parent)
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is GroupTitle -> {
                val groupTitleViewHolder = holder as GroupTitleViewHolder
                groupTitleViewHolder.titleTextView.text = item.title
            }
            is Component -> {
                val entity = item.entity
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
                                showEditableDialog(context, viewType, entity, this@EntityAdapter, holder.bindingAdapterPosition)
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
                                updateButtonColors(view, isChecked, isEnabled)
                                setOnCheckedChangeListener { _, isChecked ->
                                    entity.state = if (isChecked) "on" else "off"
                                    entity.notifyEntityChanged(preferenceManager)
                                }
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
                                onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                                    override fun onItemSelected(
                                        parent: AdapterView<*>?,
                                        view: View?,
                                        position: Int,
                                        id: Long
                                    ) {
                                        val selectedMode = parent?.getItemAtPosition(position).toString()
                                        entity.currentMode = selectedMode
                                        entity.notifyEntityChanged(preferenceManager)
                                    }

                                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                                }
                            }
                        }
                    }
                    ITEM_TYPE_SCROLLABLE_BAR -> {
                        generalHolder.views[R.id.seekBar]?.let { view ->
                            (view as? SeekBar)?.apply {
                                progress = entity.brightness
                                isEnabled = entity.enabled
                                setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                                    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                                        entity.brightness = progress
                                        entity.notifyEntityChanged(preferenceManager)
                                    }

                                    override fun onStartTrackingTouch(seekBar: SeekBar?) {
                                        // No action needed
                                    }

                                    override fun onStopTrackingTouch(seekBar: SeekBar?) {
                                        // No action needed
                                    }
                                })
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
                                            ColorEnvelopeListener { envelope, _ ->
                                                selectedColor = envelope.color
                                                entity.colorTemp = selectedColor ?: 0
                                                entity.notifyEntityChanged(preferenceManager)
                                                generalHolder.applyBackgroundColor(selectedColor ?: 0)
                                            })
                                        .setNegativeButton(
                                            "Cancel",
                                            DialogInterface.OnClickListener { dialogInterface, _ ->
                                                dialogInterface.dismiss()
                                            })
                                        .attachAlphaSlideBar(false)
                                        .attachBrightnessSlideBar(true)
                                        .setBottomSpace(12)
                                        .show()
                                }
                                val savedColor = entity.colorTemp
                                generalHolder.applyBackgroundColor(savedColor)
                                isEnabled = entity.enabled
                            }
                        }
                    }
                    ITEM_TYPE_CHECKBOX -> {
                        generalHolder.views[R.id.checkbox]?.let { view ->
                            (view as? CheckBox)?.apply {
                                isChecked = entity.state == "on"
                                isEnabled = entity.enabled
                                updateButtonColors(view, isChecked, isEnabled)
                                setOnCheckedChangeListener { _, isChecked ->
                                    entity.state = if (isChecked) "on" else "off"
                                    entity.notifyEntityChanged(preferenceManager)
                                }
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
                                            entity.state = "$year-${month + 1}-$dayOfMonth"
                                            entity.notifyEntityChanged(preferenceManager)
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
                                isFocusableInTouchMode = entity.enabled
                                addTextChangedListener(object : TextWatcher {
                                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                                    override fun afterTextChanged(s: Editable?) {
                                        entity.state = s?.toString() ?: ""
                                        entity.notifyEntityChanged(preferenceManager)
                                    }
                                })
                            }
                        }
                    }
                    ITEM_TYPE_BUTTON -> {
                        generalHolder.views[R.id.button]?.let { view ->
                            (view as? Button)?.apply {
                                setOnClickListener {
                                    // TODO: Add call
                                    entity.notifyEntityChanged(preferenceManager)
                                }
                                isEnabled = entity.enabled
                            }
                        }
                    }

                    else -> throw IllegalArgumentException("Invalid view type")
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (val item = items[position]) {
            is Component -> {
                when (item.entity.type) {
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
            is GroupTitle -> ITEM_TYPE_GROUP_TITLE
        }
    }
}
