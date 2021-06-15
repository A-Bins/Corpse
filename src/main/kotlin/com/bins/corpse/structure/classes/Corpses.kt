package com.bins.corpse.structure.classes

import com.bins.corpse.Corpse
import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.wrappers.EnumWrappers
import com.comphenix.protocol.wrappers.WrappedDataWatcher
import net.citizensnpcs.api.npc.NPC
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.PolarBear
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import java.lang.reflect.InvocationTargetException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class Corpses {

    val corpses = ArrayList<BukkitCorpse>()
    val idByNPCs = HashMap<Int, NPC>()
    val isRightClicks = HashMap<UUID, Boolean>()
    val npcByIds = HashMap<NPC, Int>()
    val hasSchedule = HashMap<NPC, Boolean>()
    val corpseHand = HashMap<Int, Int>()
    val corpseItems = HashMap<Int, ArrayList<ItemStack>>()
    val corpseInventory = HashMap<Int, Inventory>()
    fun disable(){
        corpses.forEach {
            it.corpse.destroy()
        }
        Corpse.instance.server.worlds.forEach { w ->
            w.getEntitiesByClass(PolarBear::class.java).forEach {
                if (it.customName?.contains("시체") == true) {
                    it.remove()
                }
            }
        }
    }
    class BukkitCorpse(val bear: PolarBear, val corpse: NPC, var handItem: ItemStack, val spawn: Location, val inventory: MutableList<ItemStack>){
        init {
            Corpse.corpse.corpses.add(this)
        }
        fun destroy() {
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
            val dw = WrappedDataWatcher.getEntityWatcher(bear)
            val packet = pm.createPacket(PacketType.Play.Server.ENTITY_METADATA)
            val obj = WrappedDataWatcher.WrappedDataWatcherObject(
                6,
                WrappedDataWatcher.Registry.get(EnumWrappers.getEntityPoseClass())
            )
            dw.setObject(obj, EnumWrappers.EntityPose.SLEEPING.toNms())
            packet.integers.write(0, bear.entityId)
            packet.watchableCollectionModifier.write(0, dw.watchableObjects)
            Bukkit.getOnlinePlayers().forEach {
                pm.sendServerPacket(it, packet)
            }
        }
    }
}