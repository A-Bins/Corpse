package com.bins.corpse.call.events

import com.bins.corpse.Corpse
import com.bins.corpse.Corpse.Companion.rl
import com.bins.corpse.Corpse.Companion.rt
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import java.util.*

@Suppress("DEPRECATION")
class EvtCorpseInventoryClick: Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    fun event(e: InventoryClickEvent) {
        if (e.view.title.contains("시체")) {
            if(e.currentItem?.lore?.any { it == "§f물품을 클릭시 수색합니다" || it == "§f수색 중입니다.." } == true) {
                e.isCancelled = true
                if(e.currentItem?.lore?.any { it == "§f수색 중입니다.." } == true) return
                e.inventory.contents.forEach { it.lore = listOf("§f수색 중입니다..") }
                fun search(complete: () -> Any?) {
                    if(e.whoClicked.openInventory.topInventory != e.inventory) {
                        e.inventory.contents.forEach { it.lore = listOf("§f물품을 클릭시 수색합니다") }
                        return
                    }
                    if(e.currentItem!!.itemMeta.displayName == "§7수색 중 100%") {
                        e.inventory.contents.forEach { it.lore = listOf("§f물품을 클릭시 수색합니다") }
                        complete()
                        return
                    }
                    1L.rl {
                        e.inventory.contents.forEach { i ->
                            i.editMeta {
                                it.setDisplayName("§7수색 중 ${
                                    it.displayName.split("중 ")[1].replace("%", "").toInt() + 1
                                }%")
                            }
                        }
                        search(complete)
                    }
                }
                val id = e.view.title.split(", ").toTypedArray()[1].toInt()
                search {
                    e.whoClicked.closeInventory()
                    e.whoClicked.openInventory(Corpse.corpse.find(id).inventory)
                }
            }
        }
    }
}