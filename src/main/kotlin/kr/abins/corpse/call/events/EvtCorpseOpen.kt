package kr.abins.corpse.call.events

import kr.abins.corpse.Corpse
import org.bukkit.entity.PolarBear
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.inventory.EquipmentSlot

class EvtCorpseOpen : Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    fun event(e: PlayerInteractEntityEvent) {
        val id = e.rightClicked.entityId
        fun check() = when {
            e.hand != EquipmentSlot.HAND  -> false
            e.rightClicked !is PolarBear -> false
            e.rightClicked.customName == null -> false
//            !Corpse.corpse.isRightClicks.containsKey(uuid) -> false
//            Corpse.corpse.isRightClicks[uuid]!! -> false
            Corpse.corpse.find(id) == null -> false
            !e.rightClicked.customName!!.contains("시체") -> false
            else -> true
        }
        if(!check()) return
        Corpse.corpse.find(id)?.open(e.player)
        e.isCancelled = true
    }
}