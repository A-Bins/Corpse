package com.bins.corpse.events

import com.bins.corpse.Corpse
import org.bukkit.Bukkit
import org.bukkit.entity.PolarBear
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEntityEvent

class EvtCorpseOpen : Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    fun onRightClick(e: PlayerInteractEntityEvent) {
        val id = e.rightClicked.entityId
        val uuid = e.player.uniqueId
        fun check(): Boolean {
            if (e.rightClicked !is PolarBear) return false
            if (e.rightClicked.customName == null) return false
            if (!Corpse.corpse.isRightClicks.containsKey(uuid)) return false
            if (!Corpse.corpse.corpseItems.containsKey(id)) return false
            if (Corpse.corpse.corpseInventory[id] != null)  return false
            if (!e.rightClicked.customName!!.contains("시체")) return false
            return true
        }
        if(!check()) return
        Corpse.corpse.isRightClicks[uuid] = true
        Bukkit.getScheduler().runTaskLater(
            Corpse.instance,
            Runnable { Corpse.corpse.isRightClicks[uuid] = false }, 1
        )
        Corpse.corpse.corpseInventory[id] = Bukkit.createInventory(null, 45, e.rightClicked.customName + ", " + id)
        Corpse.corpse.corpseItems[id]!!.withIndex().forEach{ (a, i) ->
            Corpse.corpse.corpseInventory[id]!!.setItem(a, i)
        }
        e.player.openInventory(Corpse.corpse.corpseInventory[id]!!)

        e.isCancelled = true
    }
}