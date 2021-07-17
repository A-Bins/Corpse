package com.bins.corpse

import com.bins.corpse.call.commands.ReloadConfig
import com.bins.corpse.call.events.EvtCorpse
import com.bins.corpse.structure.classes.Corpses
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitScheduler


class Corpse : JavaPlugin() {
    override fun onEnable() {
        instance = this
        scheduler = server.scheduler
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
        lateinit var scheduler: BukkitScheduler
            private set
        fun Long.rt(delay: Long = 1, run: Runnable) = scheduler.runTaskTimer(instance, run, delay, this)
        fun Long.rtAsync(delay: Long = 1, run: Runnable) = scheduler.runTaskTimerAsynchronously(instance, run, delay, this)
        fun Long.rl(run: Runnable) = scheduler.runTaskLater(instance, run, this)
        lateinit var instance: Corpse
         private set
        val corpse = Corpses()
    }
}
