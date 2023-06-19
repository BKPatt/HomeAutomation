package com.example.homeautomation.login

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.homeautomation.signup.HomeAutomationSignupActivity
import com.example.homeautomation.MainActivity
import com.example.homeautomation.PreferenceManager
import com.example.homeautomation.R
import okhttp3.*
// import java.io.IOException

class HomeAutomationLoginActivity : AppCompatActivity() {

    private lateinit var endpointEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var sharedPreferences: SharedPreferences
    // private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        // Initialize views
        endpointEditText = findViewById(R.id.endpoint)
        loginButton = findViewById(R.id.login)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)

        // Restore the endpoint value if it was previously stored
        val savedEndpoint = sharedPreferences.getString("endpoint", "")
        endpointEditText.setText(savedEndpoint)

        // Set click listener for login button
        loginButton.setOnClickListener {
            val endpoint = endpointEditText.text.toString()

            if (validateCredentials(endpoint)) {
                // Save the endpoint to SharedPreferences
                val editor = sharedPreferences.edit()
                editor.putString("endpoint", endpoint)
                editor.apply()

                // Save the endpoint to PreferenceManager
                val preferenceManager = PreferenceManager(this)
                preferenceManager.setEndpoint(endpoint)
                preferenceManager.setLoggedIn(true)

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Invalid endpoint", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun validateCredentials(endpoint: String): Boolean {
        // Placeholder validation logic, replace with your actual implementation
        return endpoint == "http://example.com"

        // TODO: Uncomment this when I have access to an API
//        val request = Request.Builder()
//            .url("$endpoint/api/")
//            .build()
//
//        client.newCall(request).enqueue(object : Callback {
//            override fun onResponse(call: Call, response: Response) {
//                if (response.isSuccessful) {
//                    // Save the endpoint to SharedPreferences
//                    val editor = sharedPreferences.edit()
//                    editor.putString("endpoint", endpoint)
//                    editor.apply()
//
//                    val preferenceManager = PreferenceManager(this@HomeAutomationLoginActivity)
//                    preferenceManager.setLoggedIn(true)
//
//                    val intent = Intent(this@HomeAutomationLoginActivity, MainActivity::class.java)
//                    startActivity(intent)
//                    finish()
//                } else {
//                    runOnUiThread {
//                        Toast.makeText(
//                            this@HomeAutomationLoginActivity,
//                            "Invalid endpoint",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//                }
//            }
//
//            override fun onFailure(call: Call, e: IOException) {
//                runOnUiThread {
//                    Toast.makeText(
//                        this@HomeAutomationLoginActivity,
//                        "Failed to connect to the endpoint",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            }
//        })
    }
}
