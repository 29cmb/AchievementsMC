package xyz.devcmb.achievementsMC.controllers

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import xyz.devcmb.achievementsMC.AchievementsMC
import xyz.devcmb.achievementsMC.achievements.AbstractAchievement
import xyz.devcmb.achievementsMC.achievements.KillHostileMobsAchievement

class AchievementController : IController {
    val achievements: HashMap<String, AbstractAchievement> = HashMap()
    override fun init() {
        registerAchievements()
    }

    private fun registerAchievements() {
        registerAchievement(KillHostileMobsAchievement())
    }

    private fun registerAchievement(achievement: AbstractAchievement) {
        Bukkit.getPluginManager().registerEvents(achievement, AchievementsMC.plugin)
        achievements[achievement.id] = achievement
    }

    fun incrementAchievementProgress(player: Player, id: String, amount: Int = 1) {
        // TODO
    }
}