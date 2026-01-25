package xyz.devcmb.achievementsMC.controllers

import org.bukkit.command.CommandExecutor
import xyz.devcmb.achievementsMC.AchievementsMC
import xyz.devcmb.achievementsMC.commands.*

class CommandController : IController {
    override fun init() {
        registerCommand("amc", InfoCommand())
        registerCommand("editachievements", EditAchievementsCommand())
        registerCommand("achievements", PlayerAchievementsCommand())
    }

    private fun registerCommand(name: String, executor: CommandExecutor) {
        AchievementsMC.plugin.getCommand(name)!!.setExecutor(executor)
    }
}