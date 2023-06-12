package com.example.homeautomation.navigationDrawer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.homeautomation.R

class NavigationDrawerAdapter(private val drawerItems: List<String>) :
    RecyclerView.Adapter<NavigationDrawerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.drawer_item, parent, false
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = drawerItems[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return drawerItems.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val itemText: TextView = itemView.findViewById(R.id.item_text)

        init {
            itemView.setOnClickListener {
                // Handle the click here
                val clickedItem = drawerItems[bindingAdapterPosition]
                handleDrawerItemClick(clickedItem)
            }
        }

        fun bind(item: String) {
            itemText.text = item
        }

        private fun handleDrawerItemClick(item: String) {
            Toast.makeText(itemView.context, "$item clicked", Toast.LENGTH_SHORT).show()
            // TODO: Handle clicking of different items
            // TODO: I think it would be a good idea to include groupings on the side here instead of on the main menu ( e.g. living room: )
        }
    }
}
