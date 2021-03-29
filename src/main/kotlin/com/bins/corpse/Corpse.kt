package com.bins.corpse

import com.bins.corpse.Event.Event
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.PolarBear
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.plugin.java.JavaPlugin


class Corpse : JavaPlugin() {
    override fun onEnable() {
        server.getPluginCommand("reload-config-corpse")!!.setExecutor(reCon())
        saveDefaultConfig()
        Bukkit.getLogger().info("§a땅님을 위한 평범하고 평범한 전쟁스러운 시체가 나도는 시체플러그인이 활성화되써요오!!")
        server.pluginManager.registerEvents(Event(), this)
        instance = this
        entityHider =
            EntityHider(this, EntityHider.Policy.BLACKLIST)
        Bukkit.getScheduler().runTaskTimer(this, Runnable {
            for (p in Bukkit.getOnlinePlayers()) {
                Variables.IsRightClick.putIfAbsent(p.uniqueId, false)
            }
            for (npc in Variables.Corpses) {
                for (e in npc.entity.world
                    .getEntitiesByClass(
                        PolarBear::class.java
                    )) {
                    if (e.customName != null) {
                        if (e.customName!!.contains("시체")) {
                            if (Variables.CorpsesGetEntityID.containsKey(npc)) {
                                if (e.entityId == Variables.CorpsesGetEntityID[npc]) {
                                    val bear = npc.entity.location.clone().add(
                                        Location(
                                            npc.entity.world, -0.7,
                                            (-1).toDouble(), 0.toDouble()
                                        )
                                    )
                                    bear.pitch = 0f
                                    bear.yaw = 90f
                                    e.teleport(bear)
                                }
                            }
                        }
                    }
                }
            }
        }, 0, 5)
        Bukkit.getScheduler().runTaskTimer(this, Runnable {
            for (inv in Variables.CorpsesInventory.values) {
                for (p in inv.viewers) {
                    val str = p.openInventory.title.split(", ".toRegex()).toTypedArray()[1]
                    val i = str.toInt()
                    Variables.CorpsesItemStack[i]!!.clear()
                    val npc = Variables.CorpsesEntityID[i]
                    for ((w, a) in p.openInventory.topInventory.contents.withIndex()) {
                        Variables.CorpsesItemStack[i]!!.add(a)
                        Variables.CorpsesInventory[i]!!.setItem(w, a)
                        if (Variables.CorpsesLocation[i] == w) {
                            (npc!!.entity as LivingEntity).equipment!!
                                .setItem(EquipmentSlot.HAND, a)
                        }
                        when (w) {
                            36 -> (npc!!.entity as LivingEntity).equipment!!
                                .setItem(EquipmentSlot.FEET, a)
                            37 -> (npc!!.entity as LivingEntity).equipment!!
                                .setItem(EquipmentSlot.LEGS, a)
                            38 -> (npc!!.entity as LivingEntity).equipment!!
                                .setItem(EquipmentSlot.CHEST, a)
                            39 -> (npc!!.entity as LivingEntity).equipment!!
                                .setItem(EquipmentSlot.HEAD, a)
                            40 -> (npc!!.entity as LivingEntity).equipment!!
                                .setItem(EquipmentSlot.OFF_HAND, a)
                        }
                    }
                }
            }
        }, 0, 1)
    }

    override fun onDisable() {
        Bukkit.getLogger().info("§c땅님을 위한 평범하고 평범한 전쟁스러운 시체가 나도는 시체플러그인이 비활성화되써요오!!")
        for (n in Variables.Corpses) {
            n.destroy()
        }
        for (e in Bukkit.getWorld("world")!!.entities) {
            if (e.type == EntityType.POLAR_BEAR) {
                if (e.customName!!.contains("시체")) {
                    e.remove()
                }
            }
        }
    }

    companion object {
        lateinit var instance: Corpse
        lateinit var entityHider: EntityHider
    }
}
