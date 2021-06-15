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
                val str = e.view.title.split(", ".toRegex()).toTypedArray()[1]
                val i = str.toInt()
                if (!Corpse.corpse.corpseInventory.containsKey(i)) return
                for (w in e.player.world.entities) {
                    if (w.entityId == i) {
                        Corpse.corpse.idByNPCs[i]!!.destroy()
                        Corpse.corpse.corpses.remove(Corpse.corpse.idByNPCs[i])
                        Corpse.corpse.corpseInventory.remove(i)
                        Corpse.corpse.corpseItems.remove(i)
                        Corpse.corpse.corpseHand.remove(i)
                        Corpse.corpse.npcByIds.remove(Corpse.corpse.idByNPCs[i])
                        Corpse.corpse.idByNPCs.remove(i)
                        w.remove()
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