package com.example.homeautomation.navigationDrawer

import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.homeautomation.R
import com.google.android.material.navigation.NavigationView

class NavigationDrawerHelper(
    private val activity: AppCompatActivity,
    private val drawerLayout: DrawerLayout,
    private val navigationView: NavigationView
) {
    init {
        setupNavigationDrawer()
    }

    private fun setupNavigationDrawer() {
        // Create a list of drawer items
        val drawerItems = listOf("Item 1", "Item 2", "Item 3")

        // Create an adapter for the drawer items
        val adapter = NavigationDrawerAdapter(drawerItems)

        // Set the adapter to the navigation view
        val recyclerView = navigationView.findViewById<RecyclerView>(R.id.navigation_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = adapter

        // Set up the navigation item click listener
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                // Handle other menu items if needed
            }
            drawerLayout.closeDrawer(navigationView) // Close the drawer here
            true
        }
    }

    fun openDrawer() {
        drawerLayout.openDrawer(navigationView)
    }

    fun closeDrawer() {
        drawerLayout.closeDrawer(navigationView)
    }
}


