package com.bins.corpse

import com.bins.corpse.events.EvtCorpse
import com.bins.corpse.structure.classes.Corpses
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.PolarBear
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.plugin.java.JavaPlugin


class Corpse : JavaPlugin() {
    override fun onEnable() {
        instance = this
        saveDefaultConfig()
        server.pluginManager.apply {
             EvtCorpse.objects.forEach {
                 registerEvents(it, this@Corpse)
             }
        }
        getCommand("reload-config-corpse")!!.setExecutor(ReloadConfig())

        Bukkit.getLogger().info("§a평범한 시체가 나도는 시체플러그인이 활성화되씀!")

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
        corpse.schedule()
    }

    override fun onDisable() {
        Bukkit.getLogger().info("§c땅님을 위한 평범하고 평범한 전쟁스러운 시체가 나도는 시체플러그인이 비활성화되써요오!!")
        corpse.disable()
    }

    companion object {
        lateinit var instance: Corpse
         private set
        val corpse = Corpses()
    }
}
