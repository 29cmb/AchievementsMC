package xyz.devcmb.achievementsMC.controllers

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import xyz.devcmb.achievementsMC.AchievementsMC
import xyz.devcmb.achievementsMC.ControllerDelegate
import xyz.devcmb.achievementsMC.util.DataTypes
import xyz.devcmb.achievementsMC.util.Database
import kotlin.String

class DataController : IController {
    lateinit var achievements: HashMap<String, DataTypes.AchievementData>
        private set
    var playerData: HashMap<Player, DataTypes.PlayerProgressionData> = HashMap()
        private set

    override fun init() {
        achievements = Database.getAchievements()
    }

    override fun cleanup() {
        achievements.forEach {
            Database.replicateAchievementData(it.value)
        }

        Bukkit.getOnlinePlayers().forEach {
            Database.replicatePlayerData(playerData[it]!!)
        }
    }

    fun addAchievement(
        id: String,
        tiers: Int,
        baseGoal: Int,
        goalIncrement: Int,
        baseReward: Int,
        rewardIncrement: Int,
        rewardType: String,
        rewardItem: String,
        edit: Boolean
    ) {
        val data = DataTypes.AchievementData(
            id, tiers, baseGoal, goalIncrement, baseReward, rewardIncrement, rewardType, rewardItem
        )
        achievements[id] = data

        if(!edit) {
            val achievementController: AchievementController = ControllerDelegate.getController("achievementController") as AchievementController
            achievementController.activateAchievement(id)
        }
    }

    fun removeAchievement(id: String) {
        val achievementController: AchievementController = ControllerDelegate.getController("achievementController") as AchievementController
        achievementController.deactivateAchievement(id)
        achievements.remove(id)
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player
        playerData[player] = Database.getPlayerProgressionData(player)
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        AchievementsMC.pluginLogger.info { "PlayerQuitEvent called for player" }
        val player = event.player
        Database.replicatePlayerData(playerData[player]!!)
        playerData.remove(player)
    }
}