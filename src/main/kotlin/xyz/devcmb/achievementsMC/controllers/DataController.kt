package xyz.devcmb.achievementsMC.controllers

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import xyz.devcmb.achievementsMC.AchievementsMC
import xyz.devcmb.achievementsMC.util.DataTypes
import xyz.devcmb.achievementsMC.util.Database

/*
TODO list
[x] Fetch db data on server open
[x] Replicate back to db on server close
[x] Fetch player data upon player join
[x] Replicate player data to db on player leave
[x] Replicate player data to db on server close
*/

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