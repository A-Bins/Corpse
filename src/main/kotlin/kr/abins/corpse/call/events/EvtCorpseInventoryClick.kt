package kr.abins.corpse.call.events

import kr.abins.corpse.Corpse
import kr.abins.corpse.Corpse.Companion.later
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent

@Suppress("DEPRECATION")
class EvtCorpseInventoryClick : Listener {

//    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
//    fun event(e: InventoryClickEvent) {
//        if (e.view.title.contains("시체")) {
//            if (e.currentItem?.lore?.any { it == "§f물품을 클릭시 수색합니다" || it == "§f수색 중입니다.." } == true) {
//                e.isCancelled = true
//                if (e.currentItem?.lore?.any { it == "§f수색 중입니다.." } == true) return
//                e.inventory.contents.forEach { it?.lore = listOf("§f수색 중입니다..") }
//                fun search(complete: () -> Unit) {
//                    if (e.whoClicked.openInventory.topInventory != e.inventory) {
//                        e.inventory.contents.forEach { it?.lore = listOf("§f물품을 클릭시 수색합니다") }
//                        return
//                    }
//                    if (e.currentItem!!.itemMeta.displayName == "§7수색 중 100%") {
//                        e.inventory.contents.forEach { it?.lore = listOf("§f물품을 클릭시 수색합니다") }
//                        complete()
//                        return
//                    }
//                    1L later {
//                        e.inventory.contents.forEach { i ->
//                            i?.editMeta {
//                                it.setDisplayName(
//                                    "§7수색 중 ${
//                                        it.displayName.split("중 ")[1].replace("%", "").toInt() + 1
//                                    }%"
//                                )
//                            }
//                        }
//                        search(complete)
//                    }
//                }
//
//                val id = e.view.title.split(", ").toTypedArray()[1].toInt()
//                search {
//                    e.whoClicked.closeInventory()
//                    e.whoClicked.openInventory(Corpse.corpse.find(id)?.inventory ?: return@search)
//                }
//            }
//        }
//    }
}