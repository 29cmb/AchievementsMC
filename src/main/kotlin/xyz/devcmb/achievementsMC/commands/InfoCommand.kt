package xyz.devcmb.achievementsMC.commands

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import xyz.devcmb.achievementsMC.Constants

class InfoCommand : CommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<String>
    ): Boolean {
        sender.sendMessage(
            Component.empty()
                .append(Component.text("AchievementsMC").color(NamedTextColor.YELLOW))
                .append(Component.newline())
                .append(Component.text("v" + Constants.VERSION).color(NamedTextColor.GOLD))
                .append(Component.newline())
                .append(
                    if(Constants.IS_DEVELOPMENT)
                        Component.text("Development Build").color(NamedTextColor.AQUA)
                    else
                        Component.text("Release Build").color(NamedTextColor.GREEN)
                )
        )

        return true
    }
}