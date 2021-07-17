package com.bins.corpse.call.events

import org.bukkit.entity.EntityType
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent

class EvtCorpseHit : Listener {

    @EventHandler
    fun event(e: EntityDamageEvent) {
        if (e.entity.type != EntityType.POLAR_BEAR)
            return
        if(e.entity.customName?.contains("시체") != true)
            return
        e.isCancelled = true
    }
}