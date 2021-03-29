package com.bins.corpse

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender


class reCon : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (!sender.isOp) return false
        Corpse.instance.reloadConfig()
        sender.sendMessage("§a성공적!..  §7§o마치 운명?")
        return false
    }
}
