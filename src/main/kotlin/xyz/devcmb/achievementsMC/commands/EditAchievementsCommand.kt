package xyz.devcmb.achievementsMC.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import xyz.devcmb.achievementsMC.ControllerDelegate
import xyz.devcmb.achievementsMC.controllers.UIController
import xyz.devcmb.achievementsMC.util.Format

class EditAchievementsCommand : CommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        if(sender !is Player) {
            sender.sendMessage(
                Format.format("This command can only be executed by players", Format.FormatType.INVALID)
            )

            return true
        }

        if(!sender.hasPermission("achievements-mc.edit-achievements")) {
            sender.sendMessage(Format.format("You do not have permission to use this command!", Format.FormatType.INVALID))
        }

        val uiController = ControllerDelegate.getController("uiController") as UIController
        uiController.openUI(sender, "editAchievementsChestUI")

        return true
    }

}