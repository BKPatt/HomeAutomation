package com.example.homeautomation.dropDown

import android.content.Context
import android.content.Intent
import android.view.Gravity
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import com.example.homeautomation.PreferenceManager
import com.example.homeautomation.R
import com.example.homeautomation.login.HomeAutomationLoginActivity
import com.example.homeautomation.profile.ProfileActivity

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
                openUserInfo()
                true
            }
            R.id.help -> {
                // TODO: Add help screen
                true
            }
            R.id.logout -> {
                logoutUser()
                true
            }
            else -> false
        }
    }

    private fun openUserInfo() {
        val intent = Intent(context, ProfileActivity::class.java) // Replace ProfileActivity with your user profile activity
        context.startActivity(intent)
    }

    private fun logoutUser() {
        val preferenceManager = PreferenceManager(context)
        preferenceManager.setLoggedIn(false)

        val intent = Intent(context, HomeAutomationLoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)

        if (context is AppCompatActivity) {
            context.finish()
        }
    }
}
