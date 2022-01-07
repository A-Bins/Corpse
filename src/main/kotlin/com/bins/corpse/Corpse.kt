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

        Bukkit.getLogger().info("§6[ Corpse ] made by A_Bins ( Bins#1004 current #3206 )")

        corpse.teleport()
        corpse.schedule()
    }

    override fun onDisable() {
        Bukkit.getLogger().info("§7[ Corpse ] made by A_Bins ( Bins#1004 current #3206 )")
        corpse.disable()
    }

    companion object {
        lateinit var scheduler: BukkitScheduler
            private set
        fun cancel(id: Int) = instance.server.scheduler.cancelTask(id)
        infix fun Long.timer(run: Runnable) = scheduler.runTaskTimer(instance, run, 1, this)
//        infix fun Long.timerAsync(run: Runnable) = scheduler.runTaskTimerAsynchronously(instance, run, 1, this)
//        fun taskAsync(run: Runnable) = scheduler.runTaskAsynchronously(instance, run)
//        fun task(run: Runnable) = scheduler.runTask(instance, run)
        infix fun Long.later(run: Runnable) = scheduler.runTaskLater(instance, run, this)
//        infix fun Long.laterAsync(run: Runnable) = scheduler.runTaskLaterAsynchronously(instance, run, this)
        lateinit var instance: Corpse
         private set
        val corpse = Corpses()
    }
}
