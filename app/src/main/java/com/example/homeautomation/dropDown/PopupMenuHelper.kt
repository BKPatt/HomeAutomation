package com.example.homeautomation.dropDown

import android.content.Context
import android.view.Gravity
import android.view.View
import androidx.appcompat.widget.PopupMenu
import com.example.homeautomation.R

class PopupMenuHelper(private val context: Context) {
    fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(context, view, Gravity.END, 0, R.style.PopupMenuSmaller)
        popupMenu.inflate(R.menu.account_menu)
        popupMenu.setOnMenuItemClickListener { item -> handleMenuItemClick(item.itemId) }
        popupMenu.show()
    }

    private fun handleMenuItemClick(itemId: Int): Boolean {
        return when (itemId) {
            R.id.yourProfile -> {
                // TODO: Add profile information such as home assistant url, email, phone number, etc
                true
            }
            R.id.help -> {
                // TODO: Add help screen
                true
            }
            R.id.logout -> {
                // TODO: Log out user and bring to login screen
                true
            }
            else -> false
        }
    }
}