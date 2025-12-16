package xyz.devcmb.achievementsMC.controllers

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.scheduler.BukkitRunnable
import xyz.devcmb.achievementsMC.AchievementsMC
import xyz.devcmb.achievementsMC.Constants

class DebuggingController : IController {
    val actionRunnables: HashMap<Player, BukkitRunnable> = HashMap()
    override fun init() {
    }

    @EventHandler
    fun playerJoin(event: PlayerJoinEvent) {
        val player: Player = event.player
        val runnable = object : BukkitRunnable() {
            override fun run() {
                val actionBar: Component =
                    Component.empty()
                        .append(Component.text("AchievementsMC ").color(NamedTextColor.RED))
                        .append(Component.text("|").color(NamedTextColor.WHITE))
                        .append(Component.text(" v" + Constants.VERSION).color(NamedTextColor.GRAY))
                        .append(Component.text(" (indev)").color(NamedTextColor.GOLD))

                player.sendActionBar(actionBar)
            }
        }

        runnable.runTaskTimer(AchievementsMC.plugin, 0, 20)
        actionRunnables[player] = runnable
    }

    @EventHandler
    fun playerLeave(event: PlayerQuitEvent) {
        actionRunnables[event.player]!!.cancel()
        actionRunnables.remove(event.player)
    }
}