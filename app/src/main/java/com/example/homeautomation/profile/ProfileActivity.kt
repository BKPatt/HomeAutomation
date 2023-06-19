package com.example.homeautomation.profile

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.homeautomation.PreferenceManager
import com.example.homeautomation.R

class ProfileActivity : AppCompatActivity() {
    private lateinit var endpointEditText: EditText
    private lateinit var firstNameEditText: EditText
    private lateinit var lastNameEditText: EditText
    private lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_profile)

        preferenceManager = PreferenceManager(this)

        endpointEditText = findViewById(R.id.endpoint)
        firstNameEditText = findViewById(R.id.user_firstName)
        lastNameEditText = findViewById(R.id.user_lastName)

        // Get values from SharedPref and set them in EditText fields
        var endpoint = preferenceManager.getEndpoint()
        var firstName = preferenceManager.getFirstName()
        var lastName = preferenceManager.getLastName()

        if(firstName == "0") firstName = ""
        if(lastName == "0") lastName = ""

        endpointEditText.setText(endpoint)
        firstNameEditText.setText(firstName)
        lastNameEditText.setText(lastName)

        val saveButton: Button = findViewById(R.id.saveButton)
        val cancelButton: Button = findViewById(R.id.cancelButton)

        // Store any changes temporarily, instead of saving directly to SharedPref
        endpointEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                endpoint = s.toString()
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })

        firstNameEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                firstName = s.toString()
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })

        lastNameEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                lastName = s.toString()
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })

        // Save changes to SharedPref only when 'Save' is clicked
        saveButton.setOnClickListener {
            val endpointValue = endpointEditText.text.toString().trim()
            val firstNameValue = firstNameEditText.text.toString().trim()
            val lastNameValue = lastNameEditText.text.toString().trim()

            // Validation check for required endpoint field
            if (endpointValue.isEmpty()) {
                endpointEditText.error = "Endpoint is required"
                return@setOnClickListener
            }

            // Save changes to SharedPref
            preferenceManager.setEndpoint(endpointValue)
            preferenceManager.setFirstName(firstNameValue)
            preferenceManager.setLastName(lastNameValue)
            finish()
        }


        // Discard changes and finish activity when 'Cancel' is clicked
        cancelButton.setOnClickListener {
            finish()
        }
    }
}
