package com.bins.corpse.events

import com.bins.corpse.Corpse
import com.bins.corpse.structure.classes.Corpses
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Player
import org.bukkit.entity.PolarBear
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryCloseEvent
import java.util.ArrayList

class EvtCorpseClose : Listener{
    @EventHandler
    fun onClose(e: InventoryCloseEvent) {
        if (e.view.title.contains("시체")) {
            val nullSize = e.view.topInventory.contents.filter { it == null }.size
            if (nullSize == 45) {
                val id = e.view.title.split(", ").toTypedArray()[1].toInt()
                lateinit var corpse: Corpses.BukkitCorpse
                e.player.world.getEntitiesByClass(PolarBear::class.java).filter { it.entityId == id }.forEach { w ->
                    corpse = Corpse.corpse.corpses.stream().filter { it.bear.entityId == w.entityId }.toArray()[0] as Corpses.BukkitCorpse
                }
                if(corpse.closes.contains(e.player.uniqueId)) return
                corpse.closes.add(e.player.uniqueId)
                corpse.destroy()
                e.viewers.filter { it.name != e.player.name }.forEach { p ->
                    corpse.closes.add(p.uniqueId)
                    p.closeInventory()
                }
            }
        }
    }
}