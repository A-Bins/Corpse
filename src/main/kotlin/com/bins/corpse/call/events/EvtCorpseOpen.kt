package com.bins.corpse.call.events

import com.bins.corpse.Corpse
import com.bins.corpse.Corpse.Companion.rl
import com.bins.corpse.structure.classes.Corpses
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
        fun check() = when {
            e.rightClicked !is PolarBear -> false
            e.rightClicked.customName == null -> false
            !Corpse.corpse.isRightClicks.containsKey(uuid) -> false
            Corpse.corpse.isRightClicks[uuid]!! -> false
            !Corpse.corpse.corpses.stream().anyMatch { it.bear.entityId == id } -> false
            !e.rightClicked.customName!!.contains("시체") -> false
            else -> true
        }
        if(!check()) return
        Corpse.corpse.isRightClicks[uuid] = true
        1L.rl { Corpse.corpse.isRightClicks[uuid] = false }
        Corpse.corpse.find(id).open(e.player)
        e.isCancelled = true
    }
}