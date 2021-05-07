package com.bins.corpse.Event

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.wrappers.EnumWrappers
import com.comphenix.protocol.wrappers.WrappedDataWatcher
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject
import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldedit.math.BlockVector3
import com.sk89q.worldguard.WorldGuard
import com.bins.corpse.Corpse
import com.bins.corpse.Variables
import net.citizensnpcs.api.CitizensAPI
import net.citizensnpcs.api.npc.NPC
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.*
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import java.lang.reflect.InvocationTargetException
import java.util.ArrayList
import java.util.function.Consumer
import java.util.stream.Collectors

class Event : Listener {
    @EventHandler
    fun onClose(e: InventoryCloseEvent) {
        if (e.view.title.contains("시체")) {
            var a = 0
            for (i in e.view.topInventory.contents) {
                if (i == null) {
                    a++
                }
            }
            if (a == 45) {
                val str = e.view.title.split(", ".toRegex()).toTypedArray()[1]
                val i = str.toInt()
                if (!Variables.CorpsesInventory.containsKey(i)) return
                for (w in e.player.world.entities) {
                    if (w.entityId == i) {
                        Variables.CorpsesEntityID[i]!!.destroy()
                        Variables.Corpses.remove(Variables.CorpsesEntityID[i])
                        Variables.CorpsesInventory.remove(i)
                        Variables.CorpsesItemStack.remove(i)
                        Variables.CorpsesLocation.remove(i)
                        Variables.CorpsesGetEntityID.remove(Variables.CorpsesEntityID[i])
                        Variables.CorpsesEntityID.remove(i)
                        w.remove()
                    }
                }
                val array = ArrayList<HumanEntity>()
                for (entity in e.viewers) {
                    if (entity is Player) {
                        if (e.player !== entity) {
                            array.add(entity)
                        }
                    }
                }
                array.forEach(Consumer { obj: HumanEntity -> obj.closeInventory() })
            }
        }
    }
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    fun onRightClick(e: PlayerInteractEntityEvent) {
        if (e.rightClicked !is PolarBear) return
        if (e.rightClicked.customName == null) return
        if (!Variables.IsRightClick.containsKey(e.player.uniqueId)) return
        if (!Variables.CorpsesItemStack.containsKey(e.rightClicked.entityId)) return
        Variables.IsRightClick[e.player.uniqueId] = true
        Bukkit.getScheduler().runTaskLater(
            Corpse.instance,
            Runnable { Variables.IsRightClick[e.player.uniqueId] = false }, 1
        )
        e.isCancelled = true
        if (e.rightClicked.customName!!.contains("시체")) {
            if (Variables.CorpsesInventory[e.rightClicked.entityId] == null) {
                Variables.CorpsesInventory[e.rightClicked.entityId] = Bukkit.createInventory(null, 45, e.rightClicked.customName + ", " + e.rightClicked.entityId)
                for ((a, i) in Variables.CorpsesItemStack[e.rightClicked.entityId]!!.withIndex()) {
                    Variables.CorpsesInventory[e.rightClicked.entityId]!!.setItem(a, i)
                }
            }
            e.player.openInventory(Variables.CorpsesInventory[e.rightClicked.entityId]!!)
        }
    }

    @EventHandler
    fun onDamage(e: EntityDamageEvent) {
        if (e.entity.type != EntityType.POLAR_BEAR)
            return
        if(e.entity.customName?.contains("시체") != true)
            return
        e.isCancelled = true
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    fun onDead(e: PlayerDeathEvent) {
        e.isCancelled
        val p = e.entity
        val loc = Location(
            e.entity.world,
            e.entity.location.x,
            e.entity.location.y,
            e.entity.location.z,
            0f,
            0f
        )
        if (Corpse.instance.config.getBoolean("Region-is-use?")) {
            val container = WorldGuard.getInstance().platform.regionContainer
            val regionManager = container[BukkitAdapter.adapt(p.world)]
            val w = BlockVector3.at(loc.x, loc.y, loc.z)
            val regionSet = regionManager!!.getApplicableRegions(w)
            val arrayList = ArrayList<String>()
            for (`$` in regionSet.regions) {
                arrayList.add(`$`.id)
            }
            if (!arrayList.containsAll(Corpse.instance.config.getStringList("Region-name?"))) {
                return
            }
        }
        val inv = e.entity.inventory.contents.asList()
        inv.stream().forEach { v ->
            if(!(e.drops.stream().collect(Collectors.toList()).contains(v) and (v !== null))){
                if(v is ItemStack) {
                    v.type = Material.AIR
                }
            }
        }
        val slot = e.entity.inventory.heldItemSlot
        val main = e.entity.inventory.itemInMainHand
        e.drops.clear()
        val registry = CitizensAPI.getNPCRegistry()
        val npc = registry.createNPC(EntityType.PLAYER, e.entity.name)
        Bukkit.getScheduler().runTaskLater(Corpse.instance, Runnable {
            npc.spawn(loc)
            Variables.Corpses.add(npc)
            npc.data()[NPC.NAMEPLATE_VISIBLE_METADATA] = false
            (npc.entity as Player).gameMode = GameMode.CREATIVE
            setSleePing(npc.entity)
            (npc.entity as LivingEntity).equipment?.setItem(EquipmentSlot.HAND, main)
            val bear = npc.entity.location.clone().add(
                Location(
                    npc.entity.world, -0.7,
                    (-1).toDouble(), 0.toDouble()
                )
            )
            bear.pitch = 0f
            bear.yaw = 90f
            val pb = e.entity.world.spawn(
                bear,
                PolarBear::class.java
            )
            pb.setAI(false)
            pb.setAdult()
            pb.customName = e.entity.name + "의 시체"
            pb.isInvisible = true
            pb.isSilent = true
            pb.isInvulnerable = true
            val array = ArrayList<ItemStack>()
            Bukkit.getScheduler().runTaskLater(Corpse.instance, Runnable {
                for ((i, `$`) in inv.withIndex()) {
                    array.add(`$`)
                    if (slot == i) {
                        Variables.CorpsesLocation[pb.entityId] = i
                    }
                    when (i) {
                        36 -> (npc.entity as LivingEntity).equipment!!
                            .setItem(EquipmentSlot.FEET, `$`)
                        37 -> (npc.entity as LivingEntity).equipment!!
                            .setItem(EquipmentSlot.LEGS, `$`)
                        38 -> (npc.entity as LivingEntity).equipment!!
                            .setItem(EquipmentSlot.CHEST, `$`)
                        39 -> (npc.entity as LivingEntity).equipment!!
                            .setItem(EquipmentSlot.HEAD, `$`)
                        40 -> (npc.entity as LivingEntity).equipment!!
                            .setItem(EquipmentSlot.OFF_HAND, `$`)
                    }
                }
            }, 5)
            Variables.runTaskStop[npc] = false
            object : BukkitRunnable() {
                override fun run() {
                    if (npc.entity.pose != Pose.SLEEPING) {
                        setSleePing(npc.entity)
                        for ((i, `$`) in inv.withIndex()) {
                            when (i) {
                                36 -> (npc.entity as LivingEntity).equipment!!
                                    .setItem(EquipmentSlot.FEET, `$`)
                                37 -> (npc.entity as LivingEntity).equipment!!
                                    .setItem(EquipmentSlot.LEGS, `$`)
                                38 -> (npc.entity as LivingEntity).equipment!!
                                    .setItem(EquipmentSlot.CHEST, `$`)
                                39 -> (npc.entity as LivingEntity).equipment!!
                                    .setItem(EquipmentSlot.HEAD, `$`)
                                40 -> (npc.entity as LivingEntity).equipment!!
                                    .setItem(EquipmentSlot.OFF_HAND, `$`)
                            }
                        }
                        cancel()
                    } else if (Variables.runTaskStop[npc]!!) {
                        cancel()
                    }
                }
            }.runTaskTimer(Corpse.instance, 0, 1)
            object : BukkitRunnable() {
                override fun run() {
                    Variables.runTaskStop[npc] = true
                }
            }.runTaskLater(Corpse.instance, (20 * 5).toLong())
            npc.data()[NPC.NAMEPLATE_VISIBLE_METADATA] = false
            Variables.CorpsesItemStack[pb.entityId] = array
            Variables.CorpsesEntityID[pb.entityId] = npc
            Variables.CorpsesGetEntityID[npc] = pb.entityId
            Bukkit.getScheduler().runTaskLater(Corpse.instance, Runnable {
                Variables.CorpsesInventory.remove(pb.entityId)
                Variables.CorpsesEntityID.remove(pb.entityId)
                Variables.CorpsesItemStack.remove(pb.entityId)
                Variables.CorpsesLocation.remove(pb.entityId)
                Variables.Corpses.remove(npc)
                Variables.CorpsesGetEntityID.remove(npc)
                npc.destroy()
                pb.remove()
            }, (20 * 60 * 5).toLong())
        }, 5)
    }

    companion object {
        fun setSleePing(entity: Entity) {
            val pm = ProtocolLibrary.getProtocolManager()
            val dw = WrappedDataWatcher.getEntityWatcher(entity)
            val packet = pm.createPacket(PacketType.Play.Server.ENTITY_METADATA)
            val obj = WrappedDataWatcherObject(6, WrappedDataWatcher.Registry.get(EnumWrappers.getEntityPoseClass()))
            dw.setObject(obj, EnumWrappers.EntityPose.SLEEPING.toNms())
            packet.integers.write(0, entity.entityId)
            packet.watchableCollectionModifier.write(0, dw.watchableObjects)
            try {
                for (p in Bukkit.getOnlinePlayers()) {
                    pm.sendServerPacket(p, packet)
                }
            } catch (i: InvocationTargetException) {
                i.printStackTrace()
            }
        }
    }
}