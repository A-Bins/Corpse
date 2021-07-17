package com.bins.corpse.call.commands

import com.bins.corpse.Corpse
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender


class ReloadConfig : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (!sender.isOp) return false
        Corpse.instance.reloadConfig()
        sender.sendMessage("§a끗")
        return false
    }
}
