package xyz.devcmb.achievementsMC.controllers

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import xyz.devcmb.achievementsMC.ui.PlayerUIManager

class UIController : IController {
    val playerManagers: HashMap<Player, PlayerUIManager> = HashMap()

    override fun init() {
    }

    @EventHandler
    fun playerJoin(event: PlayerJoinEvent) {
        val manager = PlayerUIManager(event.player)
        playerManagers[event.player] = manager
    }

    @EventHandler
    fun playerLeave(event: PlayerQuitEvent) {
        playerManagers.remove(event.player)
    }
}