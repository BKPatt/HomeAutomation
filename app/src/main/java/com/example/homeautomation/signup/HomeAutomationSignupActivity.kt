package com.example.homeautomation.signup

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.homeautomation.MainActivity
import com.example.homeautomation.PreferenceManager
import com.example.homeautomation.R

class HomeAutomationSignupActivity : AppCompatActivity() {

    private lateinit var preferenceManager: PreferenceManager
    private lateinit var firstNameEditText: EditText
    private lateinit var lastNameEditText: EditText
    private lateinit var signupButton: Button
    private lateinit var backButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup)

        preferenceManager = PreferenceManager(this)
        firstNameEditText = findViewById(R.id.firstName)
        lastNameEditText = findViewById(R.id.lastName)
        signupButton = findViewById(R.id.signup)
        backButton = findViewById(R.id.back)

        signupButton.setOnClickListener {
            val firstName = firstNameEditText.text.toString()
            val lastName = lastNameEditText.text.toString()

            // Store the credentials securely
            storeCredentials(firstName, lastName)

            // Perform the action to navigate to the main functionality of the app
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        backButton.setOnClickListener{
            storeCredentials("0", "0")

            // Perform the action to navigate to the main functionality of the app
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun storeCredentials(firstName: String, lastName: String) {
        // Store the credentials
        preferenceManager.setFirstName(firstName)
        preferenceManager.setLastName(lastName)
    }
}
