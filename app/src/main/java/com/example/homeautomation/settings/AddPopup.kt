package com.example.homeautomation.settings

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup.LayoutParams
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.PopupWindow
import android.widget.Spinner
import com.example.homeautomation.R

interface AddPopupListener {
    fun onSaveClicked(entityId: String, state: String, friendlyName: String, attributes: Map<String, Any>)
    fun onCancelClicked()
}

class AddPopup(context: Context) : View.OnClickListener {
    private val popupView: View = LayoutInflater.from(context).inflate(R.layout.popup_add, null)
    private val popupWindow: PopupWindow

    private val entityIdSpinner: Spinner = popupView.findViewById(R.id.spinnerEntityType)
    private val entityTypes = arrayOf("light", "climate", "brightness", "color", "checkbox", "date", "text_input", "button")

    init {
        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, entityTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        entityIdSpinner.adapter = adapter

        popupWindow = PopupWindow(
            popupView,
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT,
            true
        ).apply {
            inputMethodMode = PopupWindow.INPUT_METHOD_NEEDED
            setBackgroundDrawable(ColorDrawable(Color.WHITE))
        }
    }

    override fun onClick(p0: View?) {}
}
