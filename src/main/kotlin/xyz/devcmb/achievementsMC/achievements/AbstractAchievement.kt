package xyz.devcmb.achievementsMC.achievements

import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack
import xyz.devcmb.achievementsMC.ControllerDelegate
import xyz.devcmb.achievementsMC.controllers.AchievementController

abstract class AbstractAchievement : Listener {
    abstract val id: String
    abstract val item: ItemStack

    protected fun increment(player: Player, amount: Int = 1) {
        val achievementController = ControllerDelegate.getController("achievementController") as AchievementController
        achievementController.incrementAchievementProgress(player, this.id, amount)
    }
}