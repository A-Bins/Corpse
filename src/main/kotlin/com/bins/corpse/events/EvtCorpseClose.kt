package com.bins.corpse.events

import com.bins.corpse.Corpse
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
            var a = 0
            e.view.topInventory.contents.forEach {
                if (it == null)
                    a++
            }
            if (a == 45) {
                val i = e.view.title.split(", ".toRegex()).toTypedArray()[1].toInt()
                e.player.world.getEntitiesByClass(PolarBear::class.java).forEach { w ->
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
            TODO("if 문으로 만약에 누군가 시체를 닫을시 시체를 닫힘 /당한/ 사람은 인식하지 않도록 해야함")
        }
    }
}