package com.bins.corpse.events

import org.bukkit.event.Listener

class EvtCorpse : Listener {
    companion object {
        val objects = arrayListOf(EvtCorpse(), EvtCorpseHit(), EvtCorpseClose(), EvtCorpseOpen())
    }
}