package kr.abins.corpse.structure.objects

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit

object Util {
    val String.component: Component
        get() = Component.text(this)

    fun Any.bb() = Bukkit.broadcastMessage("$this")
}