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
                for (w in e.player.world.entities) {
                    if (w.entityId == i) {
                        Corpse.corpse.corpses.stream().filter { it.bear.entityId == w.entityId }.forEach {
                            it.destroy()
                        }
                    }
                }
                val array = ArrayList<HumanEntity>()
                for (entity in e.viewers) {
                    if (entity is Player) {
                        if (e.player !== entity) {
                            array.add(entity)
                        }
                    }
                }
                array.forEach { obj: HumanEntity -> obj.closeInventory() }
            }
        }
    }
}