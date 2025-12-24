package xyz.devcmb.achievementsMC.controllers

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import xyz.devcmb.achievementsMC.AchievementsMC
import xyz.devcmb.achievementsMC.ControllerDelegate
import xyz.devcmb.achievementsMC.achievements.AbstractAchievement
import xyz.devcmb.achievementsMC.achievements.KillHostileMobsAchievement

class AchievementController : IController {
    val achievements: HashMap<String, AbstractAchievement> = HashMap()
    val activeAchievements: ArrayList<String> = ArrayList()
    lateinit var dataController: DataController

    override fun init() {
        dataController = ControllerDelegate.getController("dataController") as DataController
        registerAllAchievements()
        dataController.achievements.forEach {
            activateAchievement(it.value.id)
        }
    }

    private fun registerAllAchievements() {
        registerAchievement(KillHostileMobsAchievement())
    }

    private fun registerAchievement(achievement: AbstractAchievement) {
        achievements[achievement.id] = achievement
    }

    fun activateAchievement(id: String) {
        val achievement = achievements[id]
        if(achievement == null) {
            throw IllegalArgumentException("Achievement with id $id does not exist")
        }

        activeAchievements.add(id)
        Bukkit.getPluginManager().registerEvents(achievement, AchievementsMC.plugin)
    }

    fun incrementAchievementProgress(player: Player, id: String, amount: Int = 1) {
        // TODO
    }
}