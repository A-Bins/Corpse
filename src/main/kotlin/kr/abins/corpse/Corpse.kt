package kr.abins.corpse

import kr.abins.corpse.call.commands.ReloadConfig
import kr.abins.corpse.call.events.EvtCorpse
import kr.abins.corpse.structure.classes.Corpses
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

        Bukkit.getLogger().info("§6[ Corpse ] made by A_Bins ( Bins#1004 )")

        corpse.teleport()
        corpse.schedule()
    }

    override fun onDisable() {
        Bukkit.getLogger().info("§7[ Corpse ] made by A_Bins ( Bins#1004 )")
        corpse.disable()
    }

    companion object {
        lateinit var scheduler: BukkitScheduler
            private set

        fun cancel(id: Int) = instance.server.scheduler.cancelTask(id)
        infix fun Number.timer(run: Runnable) = scheduler.runTaskTimer(instance, run, 1, this.toLong())
        infix fun Number.timerAsync(run: Runnable) =
            scheduler.runTaskTimerAsynchronously(instance, run, 1, this.toLong())

        fun taskAsync(run: Runnable) = scheduler.runTaskAsynchronously(instance, run)
        fun task(run: Runnable) = scheduler.runTask(instance, run)
        infix fun Number.later(run: Runnable) = scheduler.runTaskLater(instance, run, this.toLong())
        infix fun Number.laterAsync(run: Runnable) = scheduler.runTaskLaterAsynchronously(instance, run, this.toLong())
        lateinit var instance: Corpse
            private set
        val corpse = Corpses
    }
}
