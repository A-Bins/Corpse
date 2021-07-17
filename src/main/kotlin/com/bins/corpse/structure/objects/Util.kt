package com.bins.corpse.structure.objects

import net.kyori.adventure.text.Component

object Util {
    val String.component: Component
    get() = Component.text(this)
}