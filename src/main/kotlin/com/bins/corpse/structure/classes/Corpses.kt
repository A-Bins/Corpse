package com.bins.corpse.structure.classes

import com.bins.corpse.Corpse
import com.bins.corpse.structure.objects.Util.component
import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.wrappers.EnumWrappers
import com.comphenix.protocol.wrappers.WrappedDataWatcher
import net.citizensnpcs.api.npc.NPC
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.PolarBear
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

@Suppress("DEPRECATION")
class Corpses {

    val corpses = ArrayList<BukkitCorpse>()
    val isRightClicks = HashMap<UUID, Boolean>()
    fun find(id: Int) = corpses.filter { it.bear.entityId == id }.toTypedArray()[0]
    fun disable(){
        corpses.forEach { it.corpse.destroy() }
        Corpse.instance.server.worlds.forEach { w ->
            w.getEntitiesByClass(PolarBear::class.java).filter { it.customName?.contains("시체") == true }.forEach {
                it.remove()
            }
        }
    }
    fun teleport() {

        Bukkit.getScheduler().runTaskTimer(Corpse.instance, Runnable {
            corpses.forEach {

                val bear = it.corpse.entity.location.clone().add(Location(it.corpse.entity.world, -0.7, (-1).toDouble(), 0.toDouble()).apply {
                    pitch = 0f
                    yaw = 90f
                })

                it.bear.teleport(bear)
            }
        }, 0, 5)
    }
    fun schedule() {

        Bukkit.getScheduler().runTaskTimer(Corpse.instance, Runnable {
            Bukkit.getOnlinePlayers().forEach {
                isRightClicks.putIfAbsent(it.uniqueId, false)
            }
            corpses.forEach { corpse ->
                corpse.inventory.viewers.forEach { p ->
                    p.openInventory.topInventory.contents.withIndex().forEach { (w, a) ->
                        corpse.inventory.setItem(w, a)
                        when (w) {
                            corpse.hand -> (corpse.corpse.entity as LivingEntity).equipment!!.setItem(EquipmentSlot.HAND, a)
                            36 -> (corpse.corpse.entity as LivingEntity).equipment!!.setItem(EquipmentSlot.FEET, a)
                            37 -> (corpse.corpse.entity as LivingEntity).equipment!!.setItem(EquipmentSlot.LEGS, a)
                            38 -> (corpse.corpse.entity as LivingEntity).equipment!!.setItem(EquipmentSlot.CHEST, a)
                            39 -> (corpse.corpse.entity as LivingEntity).equipment!!.setItem(EquipmentSlot.HEAD, a)
                            40 -> (corpse.corpse.entity as LivingEntity).equipment!!.setItem(EquipmentSlot.OFF_HAND, a)
                        }
                    }
                }
            }
        }, 0, 1)
    }

    class BukkitCorpse(
        val bear: PolarBear,
        val corpse: NPC,
        val hand: Int,
        val handItem: ItemStack,
        val spawn: Location,
        val inventory: Inventory,
        val inventoryName: String,
        val playersInventory: HashMap<UUID, Inventory> = HashMap()
        ) {
        init {
            Corpse.corpse.corpses.add(this)
        }
        fun open(p: Player) {
            if(!playersInventory.containsKey(p.uniqueId)) {
                playersInventory[p.uniqueId] = Bukkit.createInventory(null, 45, inventoryName).apply {
                    (0..44).forEach {
                        setItem(it, ItemStack(Material.BLACK_STAINED_GLASS_PANE).apply {
                            val meta = itemMeta.apply {
                                displayName("§7수색 중 0%".component)
                                lore = listOf("§f물품을 클릭시 수색합니다")
                            }
                            itemMeta = meta
                        })
                    }
                }
            }

            if(playersInventory[p.uniqueId]!!.contents.any { it.itemMeta.displayName == "§7수색 중 100%" }) p.openInventory(inventory)
            else p.openInventory(playersInventory[p.uniqueId]!!)

        }
        fun done() {
            corpse.destroy()
            bear.remove()
        }
        fun spawn() {
            corpse.spawn(spawn)
            corpse.data()[NPC.NAMEPLATE_VISIBLE_METADATA] = false
            (corpse.entity as Player).gameMode = GameMode.CREATIVE
            sleep()
            (corpse.entity as LivingEntity).equipment?.setItem(EquipmentSlot.HAND, handItem)
        }
        fun equip() {
            inventory.withIndex().forEach { (i, v) ->
                when (i) {
                    36 -> (corpse.entity as LivingEntity).equipment!!.setItem(EquipmentSlot.FEET, v)
                    37 -> (corpse.entity as LivingEntity).equipment!!.setItem(EquipmentSlot.LEGS, v)
                    38 -> (corpse.entity as LivingEntity).equipment!!.setItem(EquipmentSlot.CHEST, v)
                    39 -> (corpse.entity as LivingEntity).equipment!!.setItem(EquipmentSlot.HEAD, v)
                    40 -> (corpse.entity as LivingEntity).equipment!!.setItem(EquipmentSlot.OFF_HAND, v)
                }
            }

            (corpse.entity as LivingEntity).equipment!!.setItem(EquipmentSlot.HAND, handItem)
        }
        fun sleep() {
            val pm = ProtocolLibrary.getProtocolManager()
            val dw = WrappedDataWatcher.getEntityWatcher(corpse.entity).apply {
                setObject(
                    WrappedDataWatcher.WrappedDataWatcherObject(6, WrappedDataWatcher.Registry.get(EnumWrappers.getEntityPoseClass())),
                    EnumWrappers.EntityPose.SLEEPING.toNms()
                )
            }
            val packet = pm.createPacket(PacketType.Play.Server.ENTITY_METADATA).apply {
                watchableCollectionModifier.write(0, dw.watchableObjects)
                integers.write(0, corpse.entity.entityId)
            }
            Bukkit.getOnlinePlayers().forEach { pm.sendServerPacket(it, packet) }
        }
    }
}