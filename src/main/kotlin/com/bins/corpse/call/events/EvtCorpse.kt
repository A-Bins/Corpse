package com.bins.corpse.call.events

import com.bins.corpse.Corpse
import com.bins.corpse.structure.classes.Corpses
import com.bins.corpse.structure.objects.Util.component
import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldedit.math.BlockVector3
import com.sk89q.worldguard.WorldGuard
import net.citizensnpcs.api.CitizensAPI
import net.citizensnpcs.api.npc.NPC
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.*
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.scheduler.BukkitRunnable
import java.util.ArrayList

class EvtCorpse : Listener {
    companion object {
        val objects = arrayListOf(EvtCorpse(), EvtCorpseHit(), EvtCorpseClose(), EvtCorpseOpen(), EvtCorpseInventoryClick())
    }
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    fun event(e: PlayerDeathEvent) {



        val p = e.entity
        val loc = Location(p.world, p.location.x, p.location.y, p.location.z, 0f, 0f)
        fun check(): Boolean {

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
                    return false
                }
            }
            return true
        }
        if(!check()) return
        e.keepInventory = true
        val list = p.inventory.contents
        val itemInHand = p.inventory.itemInMainHand
        p.inventory.clear()
        e.drops.clear()
        Bukkit.getScheduler().runTaskLater(Corpse.instance, Runnable {
            val bearLoc = loc.clone().add(Location(loc.world, -0.7, (-1).toDouble(), 0.toDouble())).apply {
                pitch = 0f
                yaw = 90f
            }
            val bear = p.world.spawn(bearLoc, PolarBear::class.java).apply {
                setAI(false)
                setAdult()
                customName = p.name + "의 시체"
                isInvisible = true
                isSilent = true
                isInvulnerable = true
            }
            val inventory = Bukkit.createInventory(null, 45, "${p.name}의 시체, ${bear.entityId}".component).apply {
                list.withIndex().forEach { (index, item) ->
                    setItem(index, item)
                }
            }
            val corpse = Corpses.BukkitCorpse(
                bear,
                CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, p.name),
                p.inventory.heldItemSlot,
                itemInHand,
                bearLoc.add(0.0, 1.0, 0.0).apply {
                    pitch = 0f
                    yaw = 0f
                },
                inventory,
                "${p.name}의 시체, ${bear.entityId}"
            )
            corpse.spawn()


            Bukkit.getScheduler().runTaskLater(Corpse.instance, Runnable {
                corpse.equip()
            }, 5)

            val task = object : BukkitRunnable() {
                override fun run() {
                    if (corpse.corpse.entity.pose != Pose.SLEEPING) {
                        corpse.sleep()
                        corpse.equip()
                        cancel()
                    }
                }
            }.runTaskTimer(Corpse.instance,0, 1).taskId

            Corpse.instance.server.scheduler.runTaskLater(Corpse.instance, Runnable {
                Corpse.instance.server.scheduler.cancelTask(task)
            }, (20 * 5).toLong())

            corpse.corpse.data()[NPC.NAMEPLATE_VISIBLE_METADATA] = false
            Bukkit.getScheduler().runTaskLater(Corpse.instance, Runnable {
                corpse.done()
            }, (20 * 60 * 5).toLong())
        }, 5)
    }
}