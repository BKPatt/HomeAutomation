package com.example.homeautomation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

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
                // Login successful, navigate to the main functionality of the app (e.g., MainActivity)
                val sharedPref = getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
                with (sharedPref.edit()) {
                    putBoolean("isLoggedIn", true)
                    apply()
                }
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                // Invalid credentials, show an error message or perform appropriate action
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