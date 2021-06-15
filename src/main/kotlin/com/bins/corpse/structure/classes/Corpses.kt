package com.bins.corpse.structure.classes

import com.bins.corpse.Corpse
import net.citizensnpcs.api.npc.NPC
import org.bukkit.entity.PolarBear
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class Corpses {

    val corpses = ArrayList<NPC>()
    val idByNPCs = HashMap<Int, NPC>()
    val isRightClicks = HashMap<UUID, Boolean>()
    val npcByIds = HashMap<NPC, Int>()
    val hasSchedule = HashMap<NPC, Boolean>()
    val corpseHand = HashMap<Int, Int>()
    val corpseItems = HashMap<Int, ArrayList<ItemStack>>()
    val corpseInventory = HashMap<Int, Inventory>()
    fun disable(){

        corpses.forEach {
            it.destroy()
        }

        Corpse.instance.server.worlds.forEach { w ->
            w.getEntitiesByClass(PolarBear::class.java).forEach {
                if (it.customName?.contains("시체") == true) {
                    it.remove()
                }
            }
        }
    }
}