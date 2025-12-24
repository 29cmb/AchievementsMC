package xyz.devcmb.achievementsMC.controllers

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import xyz.devcmb.achievementsMC.util.DataTypes
import xyz.devcmb.achievementsMC.util.Database

/*
TODO list
[x] Fetch db data on server open
[ ] Replicate back to db on server close
[ ] Fetch player data upon player join
[ ] Replicate player data to db on player leave
[ ] Replicate player data to db on server close (might call the above event, investigate this @29cmb)
*/

class DataController : IController {
    lateinit var achievements: HashMap<String, DataTypes.AchievementData>
        private set
    lateinit var playerData: HashMap<Player, DataTypes.PlayerProgressionData>
        private set

    override fun init() {
        achievements = Database.getAchievements()
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player
        playerData[player] = Database.getPlayerProgressionData(player)
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        val player = event.player
        // TODO: Replicate back to the db
        playerData.remove(player)
    }
}