package com.example.homeautomation

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.ImageButton
import androidx.drawerlayout.widget.DrawerLayout
import com.example.homeautomation.dropDown.PopupMenuHelper
import com.google.android.material.navigation.NavigationView
import com.example.homeautomation.login.HomeAutomationLoginActivity
import com.example.homeautomation.navigationDrawer.NavigationDrawerHelper
import com.example.homeautomation.settings.HomescreenSettingsActivity

class MainActivity : AppCompatActivity() {

    private lateinit var preferenceManager: PreferenceManager
    private lateinit var settingsButton: Button
    private lateinit var optionsButton: ImageButton
    private lateinit var profileButton: Button
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var navigationDrawerHelper: NavigationDrawerHelper
    private lateinit var popupMenuHelper: PopupMenuHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home)

        preferenceManager = PreferenceManager(this)
        if (!preferenceManager.isLoggedIn()) {
            val intent = Intent(this, HomeAutomationLoginActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        settingsButton = findViewById(R.id.settings)
        optionsButton = findViewById(R.id.options)
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigation_drawer)

        navigationDrawerHelper = NavigationDrawerHelper(this, drawerLayout, navigationView)

        settingsButton.setOnClickListener {
            val intent = Intent(this, HomescreenSettingsActivity::class.java)
            startActivity(intent)
        }

        optionsButton.setOnClickListener {
            if (drawerLayout.isDrawerOpen(navigationView)) {
                navigationDrawerHelper.closeDrawer()
            } else {
                navigationDrawerHelper.openDrawer()
            }
        }

        profileButton = findViewById(R.id.profile)
        profileButton.setOnClickListener { view ->
            popupMenuHelper = PopupMenuHelper(this)
            popupMenuHelper.showPopupMenu(view)
        }
    }
}
