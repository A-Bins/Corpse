package kr.abins.corpse.call.commands

import kr.abins.corpse.Corpse
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender


class ReloadConfig : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (!sender.isOp) return false
        Corpse.instance.reloadConfig()
        sender.sendMessage("§a")
        return false
    }
}
