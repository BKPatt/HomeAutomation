package com.example.homeautomation.toolbar

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import com.example.homeautomation.R

class ToolbarActivity : AppCompatActivity() {

    private lateinit var optionsButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.toolbar)

        optionsButton = findViewById(R.id.options)
        optionsButton.setOnClickListener { view ->
            showPopupMenu(view)
        }
    }

    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(this, view)
        popupMenu.inflate(R.menu.side_menu)

        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_item1 -> {
                    // Handle menu item 1 click
                    true
                }
                R.id.menu_item2 -> {
                    // Handle menu item 2 click
                    true
                }
                R.id.menu_item3 -> {
                    // Handle menu item 3 click
                    true
                }
                else -> false
            }
        }

        popupMenu.show()
    }
}
