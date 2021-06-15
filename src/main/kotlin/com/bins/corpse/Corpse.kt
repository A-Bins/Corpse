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

        corpse.teleport()
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
