package com.example.homeautomation.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.homeautomation.signup.HomeAutomationSignupActivity
import com.example.homeautomation.MainActivity
import com.example.homeautomation.PreferenceManager
import com.example.homeautomation.R

class HomeAutomationLoginActivity : AppCompatActivity() {

    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var signupButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        // Initialize views
        usernameEditText = findViewById(R.id.username)
        passwordEditText = findViewById(R.id.password)
        loginButton = findViewById(R.id.login)
        signupButton = findViewById(R.id.signup)

        // Set click listener for login button
        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (validateCredentials(username, password)) {
                val preferenceManager = PreferenceManager(this)
                preferenceManager.setLoggedIn(true)

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show()
            }
        }


        // Set click listener for signup button
        signupButton.setOnClickListener {
            // Navigate to the SignupActivity
            val intent = Intent(this, HomeAutomationSignupActivity::class.java)
            startActivity(intent)
        }
    }

    private fun validateCredentials(username: String, password: String): Boolean {
        // Placeholder validation logic, replace with your actual implementation
        return username == "example@example.com" && password == "password"
    }
}