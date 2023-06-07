package com.example.homeautomation

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import java.security.MessageDigest

class HomeAutomationSignupActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var webView: WebView
    private lateinit var instanceUrlEditText: EditText
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var signupButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup)

        sharedPreferences = getSharedPreferences("Credentials", Context.MODE_PRIVATE)
        webView = findViewById(R.id.webView)
        instanceUrlEditText = findViewById(R.id.editTextInstanceUrl)
        usernameEditText = findViewById(R.id.username)
        passwordEditText = findViewById(R.id.password)
        signupButton = findViewById(R.id.signup)

        webView.webViewClient = WebViewClient()

        signupButton.setOnClickListener {
            val instanceUrl = instanceUrlEditText.text.toString()
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            // Store the credentials securely
            storeCredentials(instanceUrl, username, password)

            // Perform the action to navigate to the main functionality of the app
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun storeCredentials(instanceUrl: String, username: String, password: String) {
        val editor = sharedPreferences.edit()

        // Hash the password for added security
        val hashedPassword = hashPassword(password)

        // Store the credentials
        editor.putString("instanceUrl", instanceUrl)
        editor.putString("username", username)
        editor.putString("password", hashedPassword)
        editor.apply()
    }

    private fun hashPassword(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashedBytes = digest.digest(password.toByteArray())
        return hashedBytes.joinToString("") { "%02x".format(it) }
    }
}