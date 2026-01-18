package xyz.devcmb.achievementsMC.controllers

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.ItemStack
import xyz.devcmb.achievementsMC.ui.PlayerUIManager

class UIController : IController {
    val playerManagers: HashMap<Player, PlayerUIManager> = HashMap()
    val eventCallbacks: HashMap<Player, HashMap<String, (item: ItemStack) -> Unit>> = HashMap()

    override fun init() {
    }

    fun openUI(player: Player, id: String) {
        playerManagers[player]!!.openUI(id)
    }

    @EventHandler
    fun playerJoin(event: PlayerJoinEvent) {
        val manager = PlayerUIManager(event.player)
        playerManagers[event.player] = manager
        eventCallbacks[event.player] = hashMapOf()
    }

    @EventHandler
    fun playerLeave(event: PlayerQuitEvent) {
        playerManagers.remove(event.player)
        eventCallbacks.remove(event.player)
    }

    @EventHandler
    fun playerDropItem(event: PlayerDropItemEvent) {
        val player = event.player
        val item = event.itemDrop.itemStack

        val playerEventCallbacks = eventCallbacks[player]
        if(playerEventCallbacks!!.containsKey("playerDropEvent")) {
            event.isCancelled = true
            playerEventCallbacks["playerDropEvent"]!!.invoke(item)
            playerEventCallbacks.remove("playerDropEvent")
        }
    }
}