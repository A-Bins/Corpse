package kr.abins.corpse.call.events

import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldedit.math.BlockVector3
import com.sk89q.worldguard.WorldGuard
import kr.abins.corpse.Corpse
import kr.abins.corpse.Corpse.Companion.cancel
import kr.abins.corpse.Corpse.Companion.later
import kr.abins.corpse.Corpse.Companion.timer
import kr.abins.corpse.structure.classes.Corpses
import kr.abins.corpse.structure.objects.Util.component
import net.citizensnpcs.api.CitizensAPI
import net.citizensnpcs.api.npc.NPC
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.EntityType
import org.bukkit.entity.PolarBear
import org.bukkit.entity.Pose
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent

class EvtCorpse : Listener {
    companion object {
        val objects =
            arrayListOf(EvtCorpse(), EvtCorpseHit(), EvtCorpseClose(), EvtCorpseOpen(), EvtCorpseInventoryClick())
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    fun event(e: PlayerDeathEvent) {
        if (!Corpse.instance.config.getBoolean("enable")) return


        val p = e.entity
        val loc = Location(p.world, p.location.x, p.location.y, p.location.z - 1.5f, 0f, 0f).toCenterLocation()


        fun check(): Boolean {
            if (Corpse.instance.config.getBoolean("Region-is-use?")) {

                WorldGuard.getInstance()
                    .platform
                    .regionContainer[BukkitAdapter.adapt(p.world)]!!
                    .getApplicableRegions(BlockVector3.at(loc.x, loc.y, loc.z))
                    .regions.map {
                        it.id
                    }.forEach { a ->
                        Corpse.instance.config.getStringList("Region-name?").forEach {
                            if (a == it) {
                                return true
                            }
                        }
                    }
                return false
            }
            return true
        }
        if (!check()) return


        e.keepInventory = true


        val list = p.inventory.contents
        val itemInHand = p.inventory.itemInMainHand
        p.inventory.clear()
        e.drops.clear()
        val bearLoc = loc.clone().add(Location(loc.world, 0.0, -1.0, 0.8)).apply {
            pitch = 0f
            yaw = 10f
        }
        val bear = p.world.spawn(bearLoc, PolarBear::class.java) {
            it.setAI(false)
            it.setAdult()
            it.customName = p.name + "의 시체"
            it.isInvisible = true
            it.isSilent = true
            it.isInvulnerable = true
        }
        val inventory = Bukkit.createInventory(null, 45, "§f${p.name}의 시체, ${bear.entityId}".component).apply {
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
            var task = 0
            task = (1L timer {
                if (corpse.corpse.isSpawned) {
                    if (corpse.corpse.entity.pose != Pose.SLEEPING) {
                        corpse.sleep()
                        corpse.equip()
                        cancel(task)
                    }
                }
            }).taskId
            (20L * 5L) later { Corpse.instance.server.scheduler.cancelTask(task) }
            corpse.equip()
        }




        corpse.corpse.data()[NPC.Metadata.NAMEPLATE_VISIBLE] = false

        (20L * 60 * Corpse.instance.config.getInt("Corpse-disappearing-time")) later { corpse.done() }
    }

}