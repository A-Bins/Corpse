package com.bins.corpse.call.events

import com.bins.corpse.Corpse
import com.bins.corpse.Corpse.Companion.later
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryCloseEvent
import java.util.*

@Suppress("DEPRECATION")
class EvtCorpseClose : Listener{
    @EventHandler
    fun event(e: InventoryCloseEvent) {
        if (e.view.title.contains("시체")) {
            val nullSize = e.view.topInventory.contents.filter { it == null }.size
            if (nullSize == 45) {
                val id = e.view.title.split(", ").toTypedArray()[1]
                Corpse.corpse.find(UUID.fromString(id))?.done()
                1L later {
                    e.inventory.close()
                }
            }
        }
    }
}