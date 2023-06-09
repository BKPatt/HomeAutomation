package com.example.homeautomation

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import android.content.SharedPreferences
import android.content.Context
import android.widget.Button
import com.example.homeautomation.login.HomeAutomationLoginActivity
import com.example.homeautomation.settings.HomescreenSettingsActivity

class MainActivity : ComponentActivity() {

    private lateinit var preferenceManager: PreferenceManager
    private lateinit var settingsButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        preferenceManager = PreferenceManager(this)
        if (!preferenceManager.isLoggedIn()) {
            val intent = Intent(this, HomeAutomationLoginActivity::class.java)
            startActivity(intent)
            finish()
            return
        } else {
            setContentView(R.layout.home)
            settingsButton = findViewById(R.id.settings)

            settingsButton.setOnClickListener {
                val intent = Intent(this, HomescreenSettingsActivity::class.java)
                startActivity(intent)
            }
        }
    }
}
