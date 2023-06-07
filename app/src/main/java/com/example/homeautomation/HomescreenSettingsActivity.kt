package com.example.homeautomation

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class HomescreenSettingsActivity : AppCompatActivity() {
    private lateinit var settingsButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home)

        settingsButton = findViewById(R.id.settings)

        settingsButton.setOnClickListener {
            // TODO: Navigate to settings either through class or xml
            // val intent = Intent(this, HomeAutomationSignupActivity::class.java)
            // startActivity(intent)
        }
    }
}