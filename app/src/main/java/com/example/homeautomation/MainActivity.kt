package com.example.homeautomation

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import android.content.SharedPreferences
import android.content.Context

class MainActivity : ComponentActivity() {
    class PreferenceManager(context: Context) {
        private val sharedPreferences: SharedPreferences = context.getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)

        fun isLoggedIn(): Boolean {
            return sharedPreferences.getBoolean("isLoggedIn", false)
        }

        fun setLoggedIn(isLoggedIn: Boolean) {
            sharedPreferences.edit().putBoolean("isLoggedIn", isLoggedIn).apply()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val preferenceManager = PreferenceManager(this)
        if (!preferenceManager.isLoggedIn()) {
            // If user is not logged in, navigate to login activity and finish main activity
            val intent = Intent(this, HomeAutomationLoginActivity::class.java)
            startActivity(intent)
            finish()
            return
        }
        else {
            // Set home.xml as the content view
            setContentView(R.layout.home)
        }
    }
}
