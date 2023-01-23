package kr.abins.corpse.call.events

import kr.abins.corpse.Corpse
import kr.abins.corpse.Corpse.Companion.later
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
                val id = e.view.title.split(", ").toTypedArray()[1].toInt()
                Corpse.corpse.find(id)?.done()
                1L later {
                    e.inventory.close()
                }
            }
        }
    }
}