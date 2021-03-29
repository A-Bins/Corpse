package com.bins.corpse

import net.citizensnpcs.api.npc.NPC
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import java.util.*


object Variables {
    val Corpses = ArrayList<NPC>()
    val CorpsesEntityID = HashMap<Int, NPC>()
    val IsRightClick = HashMap<UUID, Boolean>()
    val CorpsesGetEntityID = HashMap<NPC, Int>()
    val runTaskStop = HashMap<NPC, Boolean>()
    val CorpsesLocation = HashMap<Int, Int>()
    val CorpsesItemStack = HashMap<Int, ArrayList<ItemStack>>()
    val CorpsesInventory = HashMap<Int, Inventory>()
}
