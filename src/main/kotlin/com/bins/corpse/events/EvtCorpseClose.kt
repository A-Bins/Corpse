package com.bins.corpse.events

import com.bins.corpse.Corpse
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryCloseEvent
import java.util.ArrayList

class EvtCorpseClose : Listener{
    @EventHandler
    fun onClose(e: InventoryCloseEvent) {
        if (e.view.title.contains("시체")) {
            var a = 0
            e.view.topInventory.contents.forEach {
                if (it == null)
                    a++
            }
            if (a == 45) {
                val i = e.view.title.split(", ".toRegex()).toTypedArray()[1].toInt()
                e.player.world.entities.forEach { w ->
                    if (w.entityId == i) {
                        Corpse.corpse.corpses.stream().filter { it.bear.entityId == w.entityId }.forEach {
                            it.destroy()
                        }
                    }
                }
                e.viewers.forEach {
                    if(it.name == e.player.name) return@forEach
                    it.closeInventory()
                }
            }
        }
    }
}