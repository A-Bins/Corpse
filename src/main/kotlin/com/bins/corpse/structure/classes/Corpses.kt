package com.bins.corpse.structure.classes

import com.bins.corpse.Corpse
import com.bins.corpse.Corpse.Companion.timer
import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.wrappers.EnumWrappers
import com.comphenix.protocol.wrappers.WrappedDataWatcher
import net.citizensnpcs.api.CitizensAPI
import net.citizensnpcs.api.npc.NPC
import net.citizensnpcs.trait.SkinTrait
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.PolarBear
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import java.util.*
import kotlin.collections.ArrayList

@Suppress("DEPRECATION")
class Corpses {

    val corpses = ArrayList<BukkitCorpse>()
    fun find(id: UUID) = corpses.find { it.bear.uniqueId == id }
    fun disable(){
        corpses.forEach {
            it.corpse.destroy()
            it.bear.remove()
        }

        CitizensAPI.getNPCRegistry().saveToStore()
    }
    fun teleport() {
        5L timer  {
            corpses.forEach {

                val bear = it.corpse.entity.location.clone().add(Location(it.corpse.entity.world, 0.0, -1.0, 0.8)).apply {
                    pitch = 0f
                    yaw = 10f
                }
                if(!it.bear.isDead)  it.bear.teleport(bear)
            }
        }
    }
    fun schedule() {
        1L timer {
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
        }
    }

    class BukkitCorpse(
        val bear: PolarBear,
        val corpse: NPC,
        val hand: Int,
        val handItem: ItemStack,
        val spawn: Location,
        val inventory: Inventory,
//        val inventoryName: String,
//        val playersInventory: HashMap<UUID, Inventory> = HashMap()
        ) {
        init {
            if(corpse.name == "스캐브") {
                val trait = corpse.getOrAddTrait(SkinTrait::class.java) as SkinTrait
                trait.setSkinPersistent("scav",
                    "ccfsb+xTZbo29KTDn6JhP3VCfohbksD4O74TDvbbXJ4JLEgt9PIBMgHQrgIhxONOjiDG8lISx7UU1z12nvMCLWaanBiZM20UAUjOdNRRIaLwkmiM9CjwX3eNKN8GEnRITUjnx4Od9SbDYcSag5f8pi74y2OCWvS8q+DEBMewc8Ei2zK5cN7iswXRkr62VzQPcaz4SnBT3NFbpwXnNGeRFqKCjLf8dF5oLGaCLTQZbe6QzoZ/Zcn4+HLSPWlNkW/hiNjhTtZGqEWmfCQRtfMTTs0F/GyNtGNHoaFUrqfz0E3+7ysOCQcRCMzGeA5z/Uz4ezw3T8yLzfuwjPAPEPZQoDt084Xh9RPYy0q+wNZlKvq9zGezlZaIvmN1mnaTSeYOK8ZYf7Qw/spWGvKmvhNtlL2YpNnAY5Hr0QFjI/NduWccl8vnCxWRzmk0/QTvP6kR0lBpRavn/lPg+7PeYCPpLMlXbV8Ht4f8qP/YoUAog2mS3+b3csRS3z08D8gz6z0LU9Pqf9Nc5a/18iU8rqgoGzHNX1Sm8dcNBEgIneEc4ZzI82UCO5JI9FnmWHOvIdYsEKygEZPVvcIOimrLGrNZo0FAu1Sqk2NIvnkzwXAU6CxbRAtCEETO0BRHa49UatzdjWM+sG1QAhDKNXnwLELmF14+0jnEerUVNa6dJ85DMu0=",
                    "ewogICJ0aW1lc3RhbXAiIDogMTYyNzEyNTM4NzU2NCwKICAicHJvZmlsZUlkIiA6ICI0ZjU2ZTg2ODk2OGU0ZWEwYmNjM2M2NzRlNzQ3ODdjOCIsCiAgInByb2ZpbGVOYW1lIiA6ICJDVUNGTDE1IiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2I2M2Y0ZmI2M2UzOWViNGUzNDVlYjE3NTUzMTllN2VjYWI1MzVjZDcxNTUwNjg1ZWIyMTQzYmQ5NWI4OTRmZTQiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ=="
                )
            }
            Corpse.corpse.corpses.add(this)
        }
        fun open(p: Player) {
//            if(p.location.distance(bear.location) > 2) {
//                p.sendActionBar("§f수색하기에 너무 멀리 있습니다")
//                return
//            }
//            if(!playersInventory.containsKey(p.uniqueId)) {
//                playersInventory[p.uniqueId] = Bukkit.createInventory(null, 45, inventoryName).apply {
//                    (0..44).forEach {
//                        setItem(it, ItemStack(Material.BLACK_STAINED_GLASS_PANE).apply {
//                            val meta = itemMeta.apply {
//                                displayName("§7수색 중 0%".component)
//                                lore = listOf("§f물품을 클릭시 수색합니다")
//                            }
//                            itemMeta = meta
//                        })
//                    }
//                }
//            }

//            if(playersInventory[p.uniqueId]!!.contents.any { it.itemMeta.displayName == "§7수색 중 100%" })
                p.openInventory(inventory)
//            else p.openInventory(playersInventory[p.uniqueId]!!)

        }
        fun done() {
            Corpse.corpse.corpses.remove(this)
            corpse.destroy()
            CitizensAPI.getNPCRegistry().saveToStore()
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