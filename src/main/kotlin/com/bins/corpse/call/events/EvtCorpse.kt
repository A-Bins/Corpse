package com.bins.corpse.call.events

import com.bins.corpse.Corpse
import com.bins.corpse.Corpse.Companion.cancel
import com.bins.corpse.Corpse.Companion.later
import com.bins.corpse.Corpse.Companion.timer
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

class EvtCorpse : Listener {
    companion object {
        val objects = arrayListOf(EvtCorpse(), EvtCorpseHit(), EvtCorpseClose(), EvtCorpseOpen(), EvtCorpseInventoryClick())
    }
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    fun event(e: PlayerDeathEvent) {



        val p = e.entity
        val loc = Location(p.world, p.location.x, p.location.y, p.location.z, 0f, 0f).toCenterLocation()
        fun check(): Boolean {
            if (Corpse.instance.config.getBoolean("Region-is-use?")) {

                WorldGuard.getInstance()
                    .platform
                    .regionContainer[BukkitAdapter.adapt(p.world)]!!
                    .getApplicableRegions(BlockVector3.at(loc.x, loc.y, loc.z))
                    .regions.map {
                        it.id
                    }.forEach { a -> Corpse.instance.config.getStringList("Region-name?").forEach {
                        if (a == it) { return true }
                    } }
                return false
            }
            return true
        }
        if(!check()) return
        e.keepInventory = true
        val list = p.inventory.contents
        val itemInHand = p.inventory.itemInMainHand
        p.inventory.clear()
        e.drops.clear()

        5L later {
            val bearLoc = loc.clone().add(Location(loc.world, 0.0, -1.0, 0.8)).apply {
                pitch = 0f
                yaw = 10f
            }
            val bear = p.world.spawn(bearLoc, PolarBear::class.java).apply {
                setAI(false)
                setAdult()
                customName = p.name + "의 시체"
                isInvisible = true
                isSilent = true
                isInvulnerable = true
            }
            val inventory = Bukkit.createInventory(null, 45, "§f${p.name}의 시체, ${bear.uniqueId}".component).apply {
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
                    yaw = 90f
                },
                inventory,
//                "§f${p.name}의 시체, ${bear.entityId}"
            )
            corpse.spawn()


            5L later {
                corpse.equip()
            }

            var task = 0
            task = (1L timer {
                if (corpse.corpse.entity.pose != Pose.SLEEPING) {
                    corpse.sleep()
                    corpse.equip()
                    cancel(task)
                }
            }).taskId

            (20L * 5L) later { Corpse.instance.server.scheduler.cancelTask(task) }

            corpse.corpse.data()[NPC.NAMEPLATE_VISIBLE_METADATA] = false

            (20L * 60 * Corpse.instance.config.getInt("Corpse-disappearing-time")) later { corpse.done() }
        }
    }

}