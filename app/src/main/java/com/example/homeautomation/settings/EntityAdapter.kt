package com.example.homeautomation.settings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.homeautomation.R

class EntityAdapter(private val entities: List<HomeAssistantEntity>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val ITEM_TYPE_SWITCH = 1
        private const val ITEM_TYPE_DROPDOWN = 2
    }

    inner class SwitchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val switch: Switch = itemView.findViewById(R.id.entitySwitch)
    }

    inner class DropdownViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val labelTextView: TextView = itemView.findViewById(R.id.labelTextView)
        val optionsSpinner: Spinner = itemView.findViewById(R.id.optionsSpinner)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            ITEM_TYPE_SWITCH -> {
                val switchView = inflater.inflate(R.layout.switch_item, parent, false)
                SwitchViewHolder(switchView)
            }
            ITEM_TYPE_DROPDOWN -> {
                val dropdownView = inflater.inflate(R.layout.item_dropdown, parent, false)
                DropdownViewHolder(dropdownView)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val entity = entities[position]
        when (holder) {
            is SwitchViewHolder -> {
                holder.switch.text = entity.friendlyName
                holder.switch.isChecked = entity.state == "on"
            }
            is DropdownViewHolder -> {
                holder.labelTextView.text = entity.friendlyName
                val adapter = ArrayAdapter(
                    holder.optionsSpinner.context,
                    android.R.layout.simple_spinner_item,
                    entity.availableModes ?: listOf()
                )
                holder.optionsSpinner.adapter = adapter
            }
        }
    }


    override fun getItemCount(): Int {
        return entities.size
    }

    override fun getItemViewType(position: Int): Int {
        val entity = entities[position]
        return when (entity.type) {
            "light" -> ITEM_TYPE_SWITCH
            "climate" -> ITEM_TYPE_DROPDOWN
            else -> throw IllegalArgumentException("Invalid entity type")
        }
    }

}

